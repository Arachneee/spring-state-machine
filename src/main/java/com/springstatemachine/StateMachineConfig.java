package com.springstatemachine;

import java.util.EnumSet;
import java.util.concurrent.TimeUnit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.action.StateDoActionPolicy;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.guard.Guard;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

@Configuration
@EnableStateMachine
public class StateMachineConfig extends EnumStateMachineConfigurerAdapter<PlayerStates, PlayerEvents> {

    @Override
    public void configure(StateMachineConfigurationConfigurer<PlayerStates, PlayerEvents> config)
            throws Exception {
        config
                .withConfiguration()
                .autoStartup(true)
                .stateDoActionPolicy(StateDoActionPolicy.TIMEOUT_CANCEL)
                .stateDoActionPolicyTimeout(3, TimeUnit.SECONDS)
                .listener(listener());
    }

    @Override
    public void configure(StateMachineStateConfigurer<PlayerStates, PlayerEvents> states)
            throws Exception {
        states
                .withStates()
                .initial(PlayerStates.IDLE)
                .state(PlayerStates.ATTACK, attackStateAction(), errorAction())
                .states(EnumSet.allOf(PlayerStates.class));
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<PlayerStates, PlayerEvents> transitions)
            throws Exception {
        transitions
                .withExternal()
                .source(PlayerStates.IDLE).target(PlayerStates.MOVE).event(PlayerEvents.FIND_PLAYER).guard(guard())
                .and()
                .withExternal()
                .source(PlayerStates.IDLE).target(PlayerStates.ATTACK).event(PlayerEvents.IN_BOUND_ATTACK_RANGE)
                .action(attackAction())
                .and()
                .withExternal()
                .source(PlayerStates.MOVE).target(PlayerStates.IDLE).event(PlayerEvents.NOT_FIND_PLAYER)
                .and()
                .withExternal()
                .source(PlayerStates.MOVE).target(PlayerStates.ATTACK).event(PlayerEvents.IN_BOUND_ATTACK_RANGE)
                .and()
                .withExternal()
                .source(PlayerStates.ATTACK).target(PlayerStates.MOVE).event(PlayerEvents.OUT_BOUND_ATTACK_RANGE)
                .and()
                .withExternal()
                .source(PlayerStates.ATTACK).target(PlayerStates.IDLE).event(PlayerEvents.NOT_FIND_PLAYER);
    }

    @Bean
    public StateMachineListener<PlayerStates, PlayerEvents> listener() {
        return new StateMachineListenerAdapter<PlayerStates, PlayerEvents>() {
            @Override
            public void stateChanged(State<PlayerStates, PlayerEvents> from, State<PlayerStates, PlayerEvents> to) {
                System.out.println("State change from " + (from == null ? "null" : from.getId()) + " to " + to.getId());
            }
        };
    }

    @Bean
    public Guard<PlayerStates, PlayerEvents> guard() {
        return new Guard<PlayerStates, PlayerEvents>() {

            @Override
            public boolean evaluate(StateContext<PlayerStates, PlayerEvents> context) {
                return true;
            }
        };
    }

    @Bean
    public Action<PlayerStates, PlayerEvents> attackAction() {
        return new Action<PlayerStates, PlayerEvents>() {

            @Override
            public void execute(StateContext<PlayerStates, PlayerEvents> context) {
                System.out.println("공격");
            }
        };
    }

    @Bean
    public Action<PlayerStates, PlayerEvents> attackStateAction() {
        return new Action<PlayerStates, PlayerEvents>() {

            @Override
            public void execute(StateContext<PlayerStates, PlayerEvents> context) {
                System.out.println("Sleeping for 10 seconds...");
                try {
                    Thread.sleep(5000);
                    throw new IllegalStateException("잘못된 상태");
                } catch (InterruptedException e) {
                    System.out.println("Sleep interrupted.");
                }
                System.out.println("Sleep finished.");
            }
        };
    }

    @Bean
    public Action<PlayerStates, PlayerEvents> errorAction() {
        return new Action<PlayerStates, PlayerEvents>() {

            @Override
            public void execute(StateContext<PlayerStates, PlayerEvents> context) {
                Exception exception = context.getException();
//                String message = exception.getMessage();
            }
        };
    }

}
