package com.ttasjwi.aop.pointcut;

import com.ttasjwi.aop.member.MemberServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class ExecutionTest {

    AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
    Method helloMethod;

    @BeforeEach
    public void init() throws NoSuchMethodException {
        helloMethod = MemberServiceImpl.class.getMethod("hello", String.class);
    }

    @Test
    void printMethod() {
        // public java.lang.String com.ttasjwi.aop.member.MemberServiceImpl.hello(java.lang.String)
        log.info("helloMethod = {}", helloMethod);
    }

    @Test
    @DisplayName("가장 정확한 포인트컷")
    void exactMatch() {
        // public java.lang.String com.ttasjwi.aop.member.MemberServiceImpl.hello(java.lang.String)
        pointcut.setExpression("execution(public String com.ttasjwi.aop.member.MemberServiceImpl.hello(String))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    @DisplayName("가장 많이 생략한 포인트컷")
    void allMatch() {
        pointcut.setExpression("execution(* *(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    @DisplayName("메서드 이름을 맞춘 포인트컷")
    void nameMatch() {
        pointcut.setExpression("execution(* hello(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    @DisplayName("메서드 이름 - 접두사 포인트컷")
    void nameMatchStar1() {
        pointcut.setExpression("execution(* hel*(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    @DisplayName("메서드 이름 중 특정 문자열이 포함된 포인트컷")
    void nameMatchStar2() {
        pointcut.setExpression("execution(* *ell*(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    @DisplayName("메서드 이름 - 접미사 포인트컷")
    void nameMatchStar3() {
        pointcut.setExpression("execution(* *llo(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    @DisplayName("메서드 이름 - 실패")
    void nameMatchFalse() {
        pointcut.setExpression("execution(* nono(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isFalse();
    }

    @Test
    @DisplayName("패키지 이름, 클래스명, 메서드명을 정확하게 매칭")
    void packageExactMatch1() {
        pointcut.setExpression("execution(* com.ttasjwi.aop.member.MemberServiceImpl.hello(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    @DisplayName("패키지 이름 정확히 + 클래스명, 메서드명 생략")
    void packageExactMatch2() {
        pointcut.setExpression("execution(* com.ttasjwi.aop.member.*.*(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    @DisplayName("패키지 명 뒤에 '.' -> 직계 하위 패키지가 아니므로 실패")
    void packageExactFalse() {
        pointcut.setExpression("execution(* com.ttasjwi.aop.*.*(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isFalse();
    }

    @Test
    @DisplayName("com.ttasjwi.aop.member 하위 매핑")
    void packageMatchSubPackage1() {
        pointcut.setExpression("execution(* com.ttasjwi.aop.member..*.*(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    @DisplayName("com.ttasjwi.aop 하위 매핑")
    void packageMatchSubPackage2() {
        pointcut.setExpression("execution(* com.ttasjwi.aop..*.*(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    @DisplayName("타입 - 구체 타입 매칭")
    void typeExactMatch() {
        pointcut.setExpression("execution(* com.ttasjwi.aop.member.MemberServiceImpl.*(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    @DisplayName("타입 - 상위 타입 매칭 허용")
    void typeMatchSuperType() {
        pointcut.setExpression("execution(* com.ttasjwi.aop.member.MemberService.*(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    @DisplayName("타입 - 하위타입의 메서드 매칭")
    void typeMatchInternal() throws NoSuchMethodException {
        pointcut.setExpression("execution(* com.ttasjwi.aop.member.MemberServiceImpl.*(..))");

        Method internalMethod = MemberServiceImpl.class.getMethod("internal", String.class);
        assertThat(pointcut.matches(internalMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    @DisplayName("타입 - 상위타입 메서드 매칭에 하위타입 메서드는 포함되지 않음")
    void typeMatchNoSuperTypeMethod() throws NoSuchMethodException {
        pointcut.setExpression("execution(* com.ttasjwi.aop.member.MemberService.*(..))");
        Method internalMethod = MemberServiceImpl.class.getMethod("internal", String.class);
        assertThat(pointcut.matches(internalMethod, MemberServiceImpl.class)).isFalse();
    }

    @Test
    @DisplayName("String 타입의 파라미터 허용")
    // (String)
    void argsMatch() {
        pointcut.setExpression("execution(* *(String))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    @DisplayName("파라미터 없음")
    // ()
    void noArgsMatch() {
        pointcut.setExpression("execution(* *())");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isFalse();
    }

    @Test
    @DisplayName("정확히 하나의 파라미터, 모든 타입")
    void argsMatchStar() {
        pointcut.setExpression("execution(* *(*))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    @DisplayName("파라미터 수와 무관, 모든 타입 허용")
    // (), (Xxx), (Xxx, Xxx)
    void argsMatchAll() {
        pointcut.setExpression("execution(* *(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    @DisplayName("String 타입으로 시작하고, 모든 갯수")
    // (String), (String, Xxx), (String, Xxx, Xxx)
    void argsMatchComplex() {
        pointcut.setExpression("execution(* *(String, ..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }
}
