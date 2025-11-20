package br.edu.ifrn.PcCenter.persistencia.modelo;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Table(name = "treinadores")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CadastroTreinador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome", nullable = false, length = 100)
    @NotBlank(message = "O nome é obrigatório.")
    @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres.")
    private String nome;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    @NotBlank(message = "O e-mail é obrigatório.")
    @Email(message = "Insira um e-mail válido.")
    private String email;

    @Column(name = "senha", nullable = false, length = 255)
    @NotBlank(message = "A senha é obrigatória.")
    @Size(min = 3, message = "A senha deve ter pelo menos 3 caracteres.")
    private String senha;

    @Column(name = "data_cadastro", nullable = false)
    private LocalDate dataCadastro;
}