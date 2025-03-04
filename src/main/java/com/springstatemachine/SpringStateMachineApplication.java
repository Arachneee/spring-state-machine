package com.springstatemachine;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.statemachine.StateMachine;

@RequiredArgsConstructor
@SpringBootApplication
public class SpringStateMachineApplication implements CommandLineRunner {

    private final StateMachine<PlayerStates, PlayerEvents> stateMachine;

    public static void main(String[] args) {
        SpringApplication.run(SpringStateMachineApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            while (true) {
                String input = reader.readLine();
                if (input.equals("exit")) {
                    break;
                }
                try {
                    stateMachine.sendEvent(PlayerEvents.from(input));
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }
}
