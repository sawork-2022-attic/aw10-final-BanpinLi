package com.micropos.test;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

public class PosSimulation extends Simulation {

    HttpProtocolBuilder httpProtocol = http
            .baseUrl("http://localhost:8080/api")
            .contentTypeHeader("application/json");

    // 随机获取一个商品
    ChainBuilder getProduct = exec(
            http("get random product")
            .get("/products/random")
            .check(
                    jsonPath("$.id").saveAs("productId"),
                    jsonPath("$").saveAs("product")
            )
    ).pause(3);

    // 添加到购物车
    ChainBuilder addProduct = exec(
            http("add product")
            .post("/carts/user-#{productId}/#{productId}")
    ).pause(3);

    // 进行支付
    ChainBuilder charge = exec(
            http("charge cart")
            .post("/carts/user-#{productId}")
    ).pause(2);

    // 查看对应的订单
    ChainBuilder queryOrder = exec(
            http("query Order")
            .get("/order/user-#{productId}")
    ).pause(1);

    ScenarioBuilder scn = scenario("BasicSimulation")
            .exec(getProduct, addProduct, addProduct, addProduct, charge, queryOrder);

    {
        setUp(
                scn.injectOpen(
                         atOnceUsers(500)
                )

        ).protocols(httpProtocol);
    }

}
