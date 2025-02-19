-- Insert random movies
-- Insert data into Movies Table
INSERT INTO Movies (title, poster, genre, summary, ticket_price)
VALUES 
('The Matrix', 'https://64.media.tumblr.com/tumblr_lozy2nQrbc1qzdglao1_500.jpg', 'Sci-Fi', 'A computer hacker learns from mysterious rebels about the true nature of his reality and his role in the war against its controllers.', 12.00),
('The Lion King', 'https://64.media.tumblr.com/tumblr_lxral48ejE1qzdglao1_540.jpg', 'Animation', 'A young lion prince flees his kingdom only to learn the true meaning of responsibility and bravery.', 10.00),
('Avengers: Endgame', 'http://www.impawards.com/2019/thumbs/imp_avengers_endgame_ver4.jpg', 'Action', 'After the devastating events of Avengers: Infinity War, the Avengers assemble once again to reverse Thanos’ actions and restore balance to the universe.', 15.00),
('Joker', 'http://www.impawards.com/2019/thumbs/imp_joker_ver4.jpg', 'Drama', 'In Gotham City, mentally troubled comedian Arthur Fleck is disregarded and mistreated by society.', 14.00),
('Frozen II', 'http://www.impawards.com/2019/thumbs/imp_frozen_two_ver4.jpg', 'Animation', 'Elsa, Anna, Kristoff, Olaf, and Sven embark on a dangerous but remarkable journey to discover the origin of Elsa’s magical powers.', 11.00),
('The Dark Knight', 'http://www.impawards.com/2012/thumbs/imp_dark_knight_rises_ver4.jpg', 'Action', 'Batman faces off against the Joker, a criminal mastermind who seeks to create chaos in Gotham City.', 13.00),
('Pulp Fiction', 'http://www.impawards.com/1994/thumbs/imp_pulp_fiction_ver4.jpg', 'Crime', 'The lives of two mob hitmen, a boxer, a gangster’s wife, and a pair of diner bandits intertwine in four tales of violence and redemption.', 12.50),
('Inception', 'http://www.impawards.com/2010/thumbs/imp_inception_ver2.jpg', 'Sci-Fi', 'A thief who enters the dreams of others to steal secrets from their subconscious is given the inverse task of planting an idea into the mind of a CEO.', 13.00),
('Spider-Man: No Way Home', 'http://www.impawards.com/2021/thumbs/imp_spiderman_no_way_home_ver2.jpg', 'Action', 'Peter Parker seeks help from Doctor Strange to erase his identity as Spider-Man, but the spell goes wrong, causing the multiverse to unravel.', 14.50),
('The Godfather', 'http://www.impawards.com/1972/thumbs/imp_godfather_ver2.jpg', 'Crime', 'The aging patriarch of an organized crime dynasty transfers control of his clandestine empire to his reluctant son.', 16.00);

-- Insert data into Halls Table
INSERT INTO Halls (name, capacity)
VALUES 
('Hall_A', 16),
('Hall_B', 48),
('Hall_C', 48),
('Hall_D', 48),
('Hall_E', 48),
('Hall_F', 48),
('Hall_G', 48),
('Hall_H', 48),
('Hall_I', 48),
('Hall_J', 48);


INSERT INTO Sessions (movie_id, hall_id, session_time, available_seats)
VALUES 
(1, 1, '2025-01-09 10:00:00', 16),
(1, 2, '2025-01-09 13:00:00', 48),
(1, 3, '2025-01-09 16:00:00', 48),
(2, 4, '2025-01-09 11:00:00', 48),
(2, 5, '2025-01-09 14:00:00', 48),
(2, 6, '2025-01-09 17:00:00', 48),
(3, 7, '2025-01-09 12:00:00', 48),
(3, 8, '2025-01-09 15:00:00', 48),
(3, 9, '2025-01-09 18:00:00', 48),
(4, 10, '2025-01-09 10:30:00', 48),
(4, 1, '2025-01-09 13:30:00', 16),
(4, 2, '2025-01-09 16:30:00', 48),
(5, 3, '2025-01-09 11:30:00', 48),
(5, 4, '2025-01-09 14:30:00', 48),
(5, 5, '2025-01-09 17:30:00', 48),
(6, 6, '2025-01-09 12:30:00', 48),
(6, 7, '2025-01-09 15:30:00', 48),
(6, 8, '2025-01-09 18:30:00', 48),
(7, 9, '2025-01-09 13:00:00', 48),
(7, 10, '2025-01-09 16:00:00', 48),
(7, 1, '2025-01-09 19:00:00', 16),
(8, 2, '2025-01-09 14:00:00', 48),
(8, 3, '2025-01-09 17:00:00', 48),
(8, 4, '2025-01-09 20:00:00', 48),
(9, 5, '2025-01-09 15:00:00', 48),
(9, 6, '2025-01-09 18:00:00', 48),
(9, 7, '2025-01-09 21:00:00', 48),
(10, 8, '2025-01-09 16:00:00', 48),
(10, 9, '2025-01-09 19:00:00', 48),
(10, 10, '2025-01-09 22:00:00', 48);


-- Insert data into Seats Table

INSERT INTO Seats (hall_id, row_num, seat_number)
VALUES 
(1, 1, 1), (1, 1, 2), (1, 1, 3), (1, 1, 4),
(1, 2, 1), (1, 2, 2), (1, 2, 3), (1, 2, 4),
(1, 3, 1), (1, 3, 2), (1, 3, 3), (1, 3, 4),
(1, 4, 1), (1, 4, 2), (1, 4, 3), (1, 4, 4);

-- Insert data into Seats for Hall_B to Hall_J (48 seats: 6 rows x 8 seats each)
-- Hall_B
INSERT INTO Seats (hall_id, row_num, seat_number)
VALUES 
(2, 1, 1), (2, 1, 2), (2, 1, 3), (2, 1, 4), (2, 1, 5), (2, 1, 6), (2, 1, 7), (2, 1, 8),
(2, 2, 1), (2, 2, 2), (2, 2, 3), (2, 2, 4), (2, 2, 5), (2, 2, 6), (2, 2, 7), (2, 2, 8),
(2, 3, 1), (2, 3, 2), (2, 3, 3), (2, 3, 4), (2, 3, 5), (2, 3, 6), (2, 3, 7), (2, 3, 8),
(2, 4, 1), (2, 4, 2), (2, 4, 3), (2, 4, 4), (2, 4, 5), (2, 4, 6), (2, 4, 7), (2, 4, 8),
(2, 5, 1), (2, 5, 2), (2, 5, 3), (2, 5, 4), (2, 5, 5), (2, 5, 6), (2, 5, 7), (2, 5, 8),
(2, 6, 1), (2, 6, 2), (2, 6, 3), (2, 6, 4), (2, 6, 5), (2, 6, 6), (2, 6, 7), (2, 6, 8);

-- Hall_C
INSERT INTO Seats (hall_id, row_num, seat_number)
VALUES 
(3, 1, 1), (3, 1, 2), (3, 1, 3), (3, 1, 4), (3, 1, 5), (3, 1, 6), (3, 1, 7), (3, 1, 8),
(3, 2, 1), (3, 2, 2), (3, 2, 3), (3, 2, 4), (3, 2, 5), (3, 2, 6), (3, 2, 7), (3, 2, 8),
(3, 3, 1), (3, 3, 2), (3, 3, 3), (3, 3, 4), (3, 3, 5), (3, 3, 6), (3, 3, 7), (3, 3, 8),
(3, 4, 1), (3, 4, 2), (3, 4, 3), (3, 4, 4), (3, 4, 5), (3, 4, 6), (3, 4, 7), (3, 4, 8),
(3, 5, 1), (3, 5, 2), (3, 5, 3), (3, 5, 4), (3, 5, 5), (3, 5, 6), (3, 5, 7), (3, 5, 8),
(3, 6, 1), (3, 6, 2), (3, 6, 3), (3, 6, 4), (3, 6, 5), (3, 6, 6), (3, 6, 7), (3, 6, 8);

-- Hall_D
INSERT INTO Seats (hall_id, row_num, seat_number)
VALUES 
(4, 1, 1), (4, 1, 2), (4, 1, 3), (4, 1, 4), (4, 1, 5), (4, 1, 6), (4, 1, 7), (4, 1, 8),
(4, 2, 1), (4, 2, 2), (4, 2, 3), (4, 2, 4), (4, 2, 5), (4, 2, 6), (4, 2, 7), (4, 2, 8),
(4, 3, 1), (4, 3, 2), (4, 3, 3), (4, 3, 4), (4, 3, 5), (4, 3, 6), (4, 3, 7), (4, 3, 8),
(4, 4, 1), (4, 4, 2), (4, 4, 3), (4, 4, 4), (4, 4, 5), (4, 4, 6), (4, 4, 7), (4, 4, 8),
(4, 5, 1), (4, 5, 2), (4, 5, 3), (4, 5, 4), (4, 5, 5), (4, 5, 6), (4, 5, 7), (4, 5, 8),
(4, 6, 1), (4, 6, 2), (4, 6, 3), (4, 6, 4), (4, 6, 5), (4, 6, 6), (4, 6, 7), (4, 6, 8);

-- Hall_E
INSERT INTO Seats (hall_id, row_num, seat_number)
VALUES 
(5, 1, 1), (5, 1, 2), (5, 1, 3), (5, 1, 4), (5, 1, 5), (5, 1, 6), (5, 1, 7), (5, 1, 8),
(5, 2, 1), (5, 2, 2), (5, 2, 3), (5, 2, 4), (5, 2, 5), (5, 2, 6), (5, 2, 7), (5, 2, 8),
(5, 3, 1), (5, 3, 2), (5, 3, 3), (5, 3, 4), (5, 3, 5), (5, 3, 6), (5, 3, 7), (5, 3, 8),
(5, 4, 1), (5, 4, 2), (5, 4, 3), (5, 4, 4), (5, 4, 5), (5, 4, 6), (5, 4, 7), (5, 4, 8),
(5, 5, 1), (5, 5, 2), (5, 5, 3), (5, 5, 4), (5, 5, 5), (5, 5, 6), (5, 5, 7), (5, 5, 8),
(5, 6, 1), (5, 6, 2), (5, 6, 3), (5, 6, 4), (5, 6, 5), (5, 6, 6), (5, 6, 7), (5, 6, 8);

-- Insert data into Seats for Hall_F (48 seats: 6 rows x 8 seats each)
INSERT INTO Seats (hall_id, row_num, seat_number)
VALUES 
(6, 1, 1), (6, 1, 2), (6, 1, 3), (6, 1, 4), (6, 1, 5), (6, 1, 6), (6, 1, 7), (6, 1, 8),
(6, 2, 1), (6, 2, 2), (6, 2, 3), (6, 2, 4), (6, 2, 5), (6, 2, 6), (6, 2, 7), (6, 2, 8),
(6, 3, 1), (6, 3, 2), (6, 3, 3), (6, 3, 4), (6, 3, 5), (6, 3, 6), (6, 3, 7), (6, 3, 8),
(6, 4, 1), (6, 4, 2), (6, 4, 3), (6, 4, 4), (6, 4, 5), (6, 4, 6), (6, 4, 7), (6, 4, 8),
(6, 5, 1), (6, 5, 2), (6, 5, 3), (6, 5, 4), (6, 5, 5), (6, 5, 6), (6, 5, 7), (6, 5, 8),
(6, 6, 1), (6, 6, 2), (6, 6, 3), (6, 6, 4), (6, 6, 5), (6, 6, 6), (6, 6, 7), (6, 6, 8);

-- Insert data into Seats for Hall_G (48 seats: 6 rows x 8 seats each)
INSERT INTO Seats (hall_id, row_num, seat_number)
VALUES 
(7, 1, 1), (7, 1, 2), (7, 1, 3), (7, 1, 4), (7, 1, 5), (7, 1, 6), (7, 1, 7), (7, 1, 8),
(7, 2, 1), (7, 2, 2), (7, 2, 3), (7, 2, 4), (7, 2, 5), (7, 2, 6), (7, 2, 7), (7, 2, 8),
(7, 3, 1), (7, 3, 2), (7, 3, 3), (7, 3, 4), (7, 3, 5), (7, 3, 6), (7, 3, 7), (7, 3, 8),
(7, 4, 1), (7, 4, 2), (7, 4, 3), (7, 4, 4), (7, 4, 5), (7, 4, 6), (7, 4, 7), (7, 4, 8),
(7, 5, 1), (7, 5, 2), (7, 5, 3), (7, 5, 4), (7, 5, 5), (7, 5, 6), (7, 5, 7), (7, 5, 8),
(7, 6, 1), (7, 6, 2), (7, 6, 3), (7, 6, 4), (7, 6, 5), (7, 6, 6), (7, 6, 7), (7, 6, 8);

-- Insert data into Seats for Hall_H (48 seats: 6 rows x 8 seats each)
INSERT INTO Seats (hall_id, row_num, seat_number)
VALUES 
(8, 1, 1), (8, 1, 2), (8, 1, 3), (8, 1, 4), (8, 1, 5), (8, 1, 6), (8, 1, 7), (8, 1, 8),
(8, 2, 1), (8, 2, 2), (8, 2, 3), (8, 2, 4), (8, 2, 5), (8, 2, 6), (8, 2, 7), (8, 2, 8),
(8, 3, 1), (8, 3, 2), (8, 3, 3), (8, 3, 4), (8, 3, 5), (8, 3, 6), (8, 3, 7), (8, 3, 8),
(8, 4, 1), (8, 4, 2), (8, 4, 3), (8, 4, 4), (8, 4, 5), (8, 4, 6), (8, 4, 7), (8, 4, 8),
(8, 5, 1), (8, 5, 2), (8, 5, 3), (8, 5, 4), (8, 5, 5), (8, 5, 6), (8, 5, 7), (8, 5, 8),
(8, 6, 1), (8, 6, 2), (8, 6, 3), (8, 6, 4), (8, 6, 5), (8, 6, 6), (8, 6, 7), (8, 6, 8);

-- Insert data into Seats for Hall_I (48 seats: 6 rows x 8 seats each)
INSERT INTO Seats (hall_id, row_num, seat_number)
VALUES 
(9, 1, 1), (9, 1, 2), (9, 1, 3), (9, 1, 4), (9, 1, 5), (9, 1, 6), (9, 1, 7), (9, 1, 8),
(9, 2, 1), (9, 2, 2), (9, 2, 3), (9, 2, 4), (9, 2, 5), (9, 2, 6), (9, 2, 7), (9, 2, 8),
(9, 3, 1), (9, 3, 2), (9, 3, 3), (9, 3, 4), (9, 3, 5), (9, 3, 6), (9, 3, 7), (9, 3, 8),
(9, 4, 1), (9, 4, 2), (9, 4, 3), (9, 4, 4), (9, 4, 5), (9, 4, 6), (9, 4, 7), (9, 4, 8),
(9, 5, 1), (9, 5, 2), (9, 5, 3), (9, 5, 4), (9, 5, 5), (9, 5, 6), (9, 5, 7), (9, 5, 8),
(9, 6, 1), (9, 6, 2), (9, 6, 3), (9, 6, 4), (9, 6, 5), (9, 6, 6), (9, 6, 7), (9, 6, 8);

-- Insert data into Seats for Hall_J (48 seats: 6 rows x 8 seats each)
INSERT INTO Seats (hall_id, row_num, seat_number)
VALUES 
(10, 1, 1), (10, 1, 2), (10, 1, 3), (10, 1, 4), (10, 1, 5), (10, 1, 6), (10, 1, 7), (10, 1, 8),
(10, 2, 1), (10, 2, 2), (10, 2, 3), (10, 2, 4), (10, 2, 5), (10, 2, 6), (10, 2, 7), (10, 2, 8),
(10, 3, 1), (10, 3, 2), (10, 3, 3), (10, 3, 4), (10, 3, 5), (10, 3, 6), (10, 3, 7), (10, 3, 8),
(10, 4, 1), (10, 4, 2), (10, 4, 3), (10, 4, 4), (10, 4, 5), (10, 4, 6), (10, 4, 7), (10, 4, 8),
(10, 5, 1), (10, 5, 2), (10, 5, 3), (10, 5, 4), (10, 5, 5), (10, 5, 6), (10, 5, 7), (10, 5, 8),
(10, 6, 1), (10, 6, 2), (10, 6, 3), (10, 6, 4), (10, 6, 5), (10, 6, 6), (10, 6, 7), (10, 6, 8);


-- Insert data into Products Table
INSERT INTO Products (name, price, category, description, image, stock, tax)
VALUES
('Popcorn', 5.00, 'Snack', 'Freshly popped buttery popcorn.', 'popcorn.jpg', 50, 0.50),
('Coke', 3.00, 'Beverage', 'Cold refreshing Coca-Cola.', 'coke.jpg', 100, 0.30),
('Water', 2.00, 'Beverage', 'Bottled water.', 'water.jpg', 100, 0.20),
('Candy', 2.50, 'Snack', 'Sweet and chewy candy.', 'candy.jpg', 80, 0.25),
('Toy Spider-Man', 10.00, 'Toy', 'Spider-Man action figure.', 'spiderman_toy.jpg', 30, 1.00),
('Toy Elsa', 12.00, 'Toy', 'Elsa doll from Frozen.', 'elsa_toy.jpg', 30, 1.20),
('Nachos', 6.00, 'Snack', 'Crispy nachos with cheese dip.', 'nachos.jpg', 40, 0.60),
('Ice Cream', 4.50, 'Snack', 'Delicious vanilla ice cream.', 'ice_cream.jpg', 60, 0.45),
('Chocolate', 3.00, 'Snack', 'Milk chocolate bar.', 'chocolate.jpg', 70, 0.30),
('Smoothie', 5.50, 'Beverage', 'Mixed fruit smoothie.', 'smoothie.jpg', 50, 0.55);


-- Insert random employees (manager, admin, cashier)
INSERT INTO Employees (first_name, last_name, username, password, role)
VALUES
    ('John', 'Doe', CONCAT('manager', FLOOR(1000 + (RAND() * 9000))), 'password123', 'manager'),
    ('Jane', 'Smith', CONCAT('admin', FLOOR(1000 + (RAND() * 9000))), 'password123', 'admin'),
    ('James', 'Bond', CONCAT('cashier', FLOOR(1000 + (RAND() * 9000))), 'password123', 'cashier'),
    ('Emily', 'Davis', CONCAT('cashier', FLOOR(1000 + (RAND() * 9000))), 'password123', 'cashier'),
    ('Mark', 'Taylor', CONCAT('admin', FLOOR(1000 + (RAND() * 9000))), 'password123', 'admin');
