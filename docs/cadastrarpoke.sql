CREATE TABLE pokemons (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    treinador_id BIGINT NOT NULL,
    nome VARCHAR(100) NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    habilidade VARCHAR(100),
    nivel INT,
    FOREIGN KEY (treinador_id) REFERENCES treinadores(id)
);