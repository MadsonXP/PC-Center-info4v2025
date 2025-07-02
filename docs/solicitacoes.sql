CREATE TABLE solicitacoes_troca (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    treinador_solicitante_id BIGINT NOT NULL,
    pokemon_oferecido_id BIGINT NOT NULL,
    treinador_receptor_id BIGINT NOT NULL,
    pokemon_solicitado_id BIGINT, -- Opcional, se a troca for por um Pokémon específico
    status_troca ENUM('PENDENTE', 'ACEITA', 'RECUSADA', 'CONCLUIDA') NOT NULL,
    data_solicitacao DATETIME NOT NULL,
    FOREIGN KEY (treinador_solicitante_id) REFERENCES treinadores(id),
    FOREIGN KEY (pokemon_oferecido_id) REFERENCES pokemons(id),
    FOREIGN KEY (treinador_receptor_id) REFERENCES treinadores(id),
    FOREIGN KEY (pokemon_solicitado_id) REFERENCES pokemons(id)
);