package br.edu.ifrn.PcCenter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories; // NOVO IMPORT

// NOVO: Adiciona anotação para forçar o gerenciamento JPA e Transações
@SpringBootApplication
@EnableJpaRepositories 
public class Application {
public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
    
}
    
}