package com.ttasjwi.aop.order.aop;

import org.aspectj.lang.annotation.Pointcut;

public class Pointcuts {


    // com.ttasjwi.aop.order 패키지와 하위 패키지
    @Pointcut("execution(* com.ttasjwi.aop.order..*(..))")
    public void allOrder() {}

    // 클래스 이름 패턴이 *Service 인 것들
    @Pointcut("execution(* *..*Service.*(..))")
    public void allService() {}

    // allOrder && allService
    @Pointcut("allOrder() && allService()")
    public void orderAndService() {}
}
