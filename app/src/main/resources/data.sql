-- ####################################################################
-- 1. TREINADORES (USUÁRIOS)
-- Senha para todos: 123 (USANDO NOVOS HASHES BCrypt)
-- ####################################################################

INSERT INTO treinadores (nome, email, senha, data_cadastro) VALUES
('Ash Ketchum', 'ash.ketchum@example.com', '$2a$10$f/9q1Z3v5E7h9X1y5b8w4u.Y2/C5gJ7k9D3eL4o0p.R8A5T7hM', '1997-04-01'), -- ID 1 (ADMIN)
('Misty Waterflower', 'misty.w@example.com', '$2a$10$t/3c2D1x8L4k9P5m7r1a6V.Q4W0j9I2s1F3b4G5h6J.T9U2vX0wY', '1998-07-15'), -- ID 2 (USER)
('Brock Stone', 'brock.s@example.com', '$2a$10$g/6h0L9p8A3s4D2f1J7k5L.Z1X3c5V8b2N4m6Q9w0E1r2T4y6U8I9O', '1997-04-01'); -- ID 3 (USER)


-- ####################################################################
-- 2. POKÉMONS DISPONÍVEIS PARA TROCA
-- ####################################################################

INSERT INTO pokemons (treinador_id, nome, tipo, habilidade, nivel) VALUES
(1, 'Pikachu', 'Elétrico', 'Estática', 25),      -- ID 1 (Pertence ao Ash)
(1, 'Charmander', 'Fogo', 'Chama', 18),         -- ID 2 (Pertence ao Ash)
(2, 'Starmie', 'Água/Psíquico', 'Iluminar', 30), -- ID 3 (Pertence à Misty)
(2, 'Psyduck', 'Água', 'Nervosismo', 15),       -- ID 4 (Pertence à Misty)
(3, 'Onix', 'Pedra/Solo', 'Cabeça Dura', 40),    -- ID 5 (Pertence ao Brock)
(3, 'Geodude', 'Pedra/Solo', 'Força Bruta', 20);  -- ID 6 (Pertence ao Brock)


-- ####################################################################
-- 3. LISTA DE INTERESSES (POKÉMONS DESEJADOS)
-- ####################################################################

INSERT INTO lista_interesses (treinador_id, pokemon_desejado_nome, pokemon_desejado_tipo, observacoes, data_interesse) VALUES
(1, 'Charizard', 'Fogo/Voador', 'Procurando um Charizard forte!', '2024-06-20'), 
(2, 'Gyarados', 'Água/Voador', 'Preciso de um Gyarados forte.', '2024-06-21'),    
(3, 'Steelix', 'Aço/Solo', 'Quero evoluir meu Onix!', '2024-06-22');             


-- ####################################################################
-- 4. SOLICITAÇÕES DE TROCA
-- ####################################################################

INSERT INTO solicitacoes_troca (treinador_solicitante_id, pokemon_oferecido_id, treinador_receptor_id, pokemon_solicitado_id, status_troca, data_solicitacao) VALUES
(1, 2, 2, 3, 'PENDENTE', CURRENT_TIMESTAMP()),
(2, 4, 3, NULL, 'PENDENTE', CURRENT_TIMESTAMP()),
(3, 6, 1, 1, 'RECUSADA', CURRENT_TIMESTAMP());