

-- Inserir dados de exemplo em 'treinadores'
-- Inserir dados de exemplo em 'treinadores'
-- Inserir dados de exemplo em 'treinadores'
INSERT INTO treinadores (nome, email, senha, data_cadastro) VALUES
-- Senha para todos: 123 (hash BCrypt)
('Ash Ketchum', 'ash.ketchum@example.com', '123', '1997-04-01'), 
('Misty Waterflower', 'misty.w@example.com', '$2a$10$7XvW8Gv6D/9ZpL2l5h7b4.A3.p/L5fX0gN4eH6fJ8k.Q1/3E9hM', '1998-07-15'), 
('Brock Stone', 'brock.s@example.com', '$2a$10$7XvW8Gv6D/9ZpL2l5h7b4.A3.p/L5fX0gN4eH6fJ8k.Q1/3E9hM', '1997-04-01');

/*
-- Seções de Pokémons e Solicitações mantidas comentadas para simplificar
-- Devem ser descomentadas se você precisar dos dados de teste no H2
*/

//* -- Inserir dados de exemplo em 'pokemons'
-- Assumindo que os IDs dos treinadores são 1, 2 e 3, respectivamente,
-- gerados automaticamente pela coluna PRIMARY KEY AUTO_INCREMENT.
INSERT INTO pokemons (treinador_id, nome, tipo, habilidade, nivel) VALUES
(1, 'Pikachu', 'Elétrico', 'Estática', 25),
(1, 'Charmander', 'Fogo', 'Chama', 18),
(2, 'Starmie', 'Água/Psíquico', 'Iluminar', 30),
(2, 'Psyduck', 'Água', 'Nervosismo', 15),
(3, 'Onix', 'Pedra/Solo', 'Cabeça Dura', 40),
(3, 'Geodude', 'Pedra/Solo', 'Força Bruta', 20);

-- Inserir dados de exemplo em 'lista_interesses'
INSERT INTO lista_interesses (treinador_id, pokemon_desejado_nome, pokemon_desejado_tipo, observacoes, data_interesse) VALUES
(1, 'Charizard', 'Fogo/Voador', 'Procurando um Charizard para evoluir meu Charmander!', '2024-06-20'),
(2, 'Gyarados', 'Água/Voador', 'Preciso de um Gyarados forte para a minha equipe aquática.', '2024-06-21'),
(3, 'Steelix', 'Aço/Solo', 'Quero evoluir meu Onix, troco por um Steelix!', '2024-06-22');

-- Inserir dados de exemplo em 'solicitacoes_troca'
-- Os IDs de Pokémon referenciados (pokemon_oferecido_id, pokemon_solicitado_id)
-- são da tabela 'pokemons' e os IDs de treinador são da tabela 'treinadores'.
INSERT INTO solicitacoes_troca (treinador_solicitante_id, pokemon_oferecido_id, treinador_receptor_id, pokemon_solicitado_id, status_troca, data_solicitacao) VALUES
-- Ash (ID 1) oferece Charmander (ID 2) para Misty (ID 2) por Starmie (ID 3)
(1, 2, 2, 3, 'PENDENTE', '2024-07-01 10:00:00'),
-- Misty (ID 2) oferece Psyduck (ID 4) para Brock (ID 3) sem pedir um Pokémon específico em troca
(2, 4, 3, NULL, 'PENDENTE', '2024-07-01 11:30:00'),
-- Brock (ID 3) oferece Geodude (ID 6) para Ash (ID 1) por Pikachu (ID 1)
(3, 6, 1, 1, 'RECUSADA', '2024-07-01 14:00:00'); *//