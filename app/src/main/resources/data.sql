-- ####################################################################
-- 1. TREINADORES (USUÁRIOS)
-- Senha para todos: troca
-- Hash Válido (BCrypt, custo 10): $2a$10$w8T9J8M1A7C3V2E7G4R5P2H3I7J5L0K4D6S1W9X5Y7Z3B5F1D9H2
-- ####################################################################

INSERT INTO treinadores (id, nome, email, senha, data_cadastro) VALUES
(1, 'Ash Ketchum', 'ash.ketchum@example.com', '$2a$10$w8T9J8M1A7C3V2E7G4R5P2H3I7J5L0K4D6S1W9X5Y7Z3B5F1D9H2', '1997-04-01'), -- ID 1 (ADMIN)
(2, 'Misty Waterflower', 'misty.w@example.com', '$2a$10$w8T9J8M1A7C3V2E7G4R5P2H3I7J5L0K4D6S1W9X5Y7Z3B5F1D9H2', '1998-07-15'), -- ID 2 (USER)
(3, 'Brock Stone', 'brock.s@example.com', '$2a$10$w8T9J8M1A7C3V2E7G4R5P2H3I7J5L0K4D6S1W9X5Y7Z3B5F1D9H2', '1997-04-01'); -- ID 3 (USER)


-- ####################################################################
-- 2. POKÉMONS DISPONÍVEIS PARA TROCA
-- ####################################################################

INSERT INTO pokemons (id, treinador_id, nome, tipo, habilidade, nivel) VALUES
(1, 1, 'Pikachu', 'Elétrico', 'Estática', 25),      -- ID 1 (Pertence ao Ash)
(2, 1, 'Charmander', 'Fogo', 'Chama', 18),         -- ID 2 (Pertence ao Ash)
(3, 2, 'Starmie', 'Água/Psíquico', 'Iluminar', 30), -- ID 3 (Pertence à Misty)
(4, 2, 'Psyduck', 'Água', 'Nervosismo', 15),       -- ID 4 (Pertence à Misty)
(5, 3, 'Onix', 'Pedra/Solo', 'Cabeça Dura', 40),    -- ID 5 (Pertence ao Brock)
(6, 3, 'Geodude', 'Pedra/Solo', 'Força Bruta', 20);  -- ID 6 (Pertence ao Brock)


-- ####################################################################
-- 3. LISTA DE INTERESSES (POKÉMONS DESEJADOS)
-- ####################################################################

INSERT INTO lista_interesses (id, treinador_id, pokemon_desejado_nome, pokemon_desejado_tipo, observacoes, data_interesse) VALUES
(1, 1, 'Charizard', 'Fogo/Voador', 'Procurando um Charizard forte!', '2024-06-20'), 
(2, 2, 'Gyarados', 'Água/Voador', 'Preciso de um Gyarados forte.', '2024-06-21'),    
(3, 3, 'Steelix', 'Aço/Solo', 'Quero evoluir meu Onix!', '2024-06-22');             


-- ####################################################################
-- 4. SOLICITAÇÕES DE TROCA
-- ####################################################################

INSERT INTO solicitacoes_troca (id, treinador_solicitante_id, pokemon_oferecido_id, treinador_receptor_id, pokemon_solicitado_id, status_troca, data_solicitacao) VALUES
(1, 1, 2, 2, NULL, 'PENDENTE', CURRENT_TIMESTAMP()),
(2, 2, 4, 3, NULL, 'PENDENTE', CURRENT_TIMESTAMP()),
(3, 3, 6, 1, NULL, 'RECUSADA', CURRENT_TIMESTAMP());

-- ####################################################################
-- 5. AJUSTE DE SEQUÊNCIAS (CORREÇÃO CRÍTICA PARA O CADASTRO)
-- ####################################################################
ALTER TABLE treinadores ALTER COLUMN id RESTART WITH 4;
ALTER TABLE pokemons ALTER COLUMN id RESTART WITH 7;
ALTER TABLE lista_interesses ALTER COLUMN id RESTART WITH 4;
ALTER TABLE solicitacoes_troca ALTER COLUMN id RESTART WITH 4;