CREATE TABLE lista_interesses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    treinador_id BIGINT NOT NULL,
    pokemon_desejado_nome VARCHAR(100) NOT NULL, -- Pode ser o nome do Pokémon desejado se não for um ID específico
    pokemon_desejado_tipo VARCHAR(50),
    observacoes TEXT,
    data_interesse DATE NOT NULL,
    FOREIGN KEY (treinador_id) REFERENCES treinadores(id)
);