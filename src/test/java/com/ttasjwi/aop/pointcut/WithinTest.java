package com.ttasjwi.aop.pointcut;

import com.ttasjwi.aop.member.MemberServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

public class WithinTest {

    AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
    Method helloMethod;

    @BeforeEach
    public void init() throws NoSuchMethodException {
        helloMethod = MemberServiceImpl.class.getMethod("hello", String.class);
    }

    @Test
    @DisplayName("정확하게 타입 기입")
    void withinExact() {
        pointcut.setExpression("within(com.ttasjwi.aop.member.MemberServiceImpl)");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    @DisplayName("타입에 '*' 사용")
    void withinStar() {
        pointcut.setExpression("within(com.ttasjwi.aop.member.*Service*)");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    @DisplayName("패키지명 '..' 사용")
    void withinSubPackage() {
        pointcut.setExpression("within(com.ttasjwi.aop..*)");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    @DisplayName("타겟의 타입에만 직접 적용, 인터페이스를 선정하면 안 된다.")
    void withinSuperTypeFalse() {
        pointcut.setExpression("within(com.ttasjwi.aop.member.MemberService)");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isFalse();
    }

    @Test
    @DisplayName("exection은 타입 기반, 인터페이스를 선정 가능.")
    void executionSuperTypeTrue() {
        pointcut.setExpression("execution(* com.ttasjwi.aop.member.MemberService.*(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }
}
