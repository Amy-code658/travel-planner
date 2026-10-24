
CREATE DATABASE IF NOT EXISTS tourism_db;
USE tourism_db;

-- 1. Table: users
CREATE TABLE IF NOT EXISTS users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('CUSTOMER', 'ADMIN') DEFAULT 'CUSTOMER',
    phone VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Table: destinations
CREATE TABLE IF NOT EXISTS destinations (
    destination_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    country VARCHAR(100) NOT NULL,
    description TEXT,
    category ENUM('Adventure', 'Beach', 'Culture', 'Nature', 'Luxury') NOT NULL,
    estimated_daily_cost DECIMAL(10, 2) NOT NULL,
    image_url VARCHAR(255)
);

-- 3. Table: travel_packages
CREATE TABLE IF NOT EXISTS travel_packages (
    package_id INT AUTO_INCREMENT PRIMARY KEY,
    destination_id INT NOT NULL,
    title VARCHAR(150) NOT NULL,
    description TEXT,
    duration_days INT NOT NULL,
    base_price DECIMAL(10, 2) NOT NULL,
    season VARCHAR(50),
    discount_percentage DECIMAL(5, 2) DEFAULT 0.00,
    is_active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (destination_id) REFERENCES destinations(destination_id) ON DELETE CASCADE
);

-- 4. Table: itinerary_days
CREATE TABLE IF NOT EXISTS itinerary_days (
    itinerary_id INT AUTO_INCREMENT PRIMARY KEY,
    package_id INT NOT NULL,
    day_number INT NOT NULL,
    title VARCHAR(150) NOT NULL,
    activity_description TEXT NOT NULL,
    FOREIGN KEY (package_id) REFERENCES travel_packages(package_id) ON DELETE CASCADE
);

-- 5. Table: bookings
CREATE TABLE IF NOT EXISTS bookings (
    booking_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    package_id INT NOT NULL,
    travel_date DATE NOT NULL,
    number_of_guests INT NOT NULL,
    total_cost DECIMAL(10, 2) NOT NULL,
    booking_status ENUM('CONFIRMED', 'CANCELLED', 'COMPLETED') DEFAULT 'CONFIRMED',
    payment_status VARCHAR(50) DEFAULT 'PAID',
    refund_amount DECIMAL(10, 2) DEFAULT 0.00,
    booking_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (package_id) REFERENCES travel_packages(package_id) ON DELETE CASCADE
);

-- 6. Table: reviews
CREATE TABLE IF NOT EXISTS reviews (
    review_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    package_id INT NOT NULL,
    rating INT CHECK (rating BETWEEN 1 AND 5),
    comment TEXT,
    review_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (package_id) REFERENCES travel_packages(package_id) ON DELETE CASCADE
);

-- SEED DATA (Default Accounts, Destinations, Packages & Itineraries)

-- 1. Default Users (Pass: admin123 and user123 hashed via BCrypt)
INSERT INTO users (user_id, full_name, email, password_hash, role, phone) VALUES
(1, 'System Administrator', 'admin@tourism.com', '$2a$12$ARX4KAJUFDAdCgPpcyxPteC/GJTJs2H7aCNoJng97qeOCss9bRy4S', 'ADMIN', '9998887770'),
(2, 'John Doe', 'john@gmail.com', '$2a$12$dn4XRmy10YyICz8eyG.i9.R2WbhDj7tNNRks49tQEdzXHXbyV5r7u', 'CUSTOMER', '9876543210')
ON DUPLICATE KEY UPDATE password_hash=VALUES(password_hash);

-- 2. Destinations
INSERT INTO destinations (destination_id, name, country, description, category, estimated_daily_cost, image_url) VALUES
(1, 'Bali', 'Indonesia', 'Tropical paradise known for iconic beaches, volcanic mountains, and coral reefs.', 'Beach', 60.00, 'bali.jpg'),
(2, 'Swiss Alps', 'Switzerland', 'Magnificent alpine region perfect for hiking, skiing, and breathtaking scenic trains.', 'Adventure', 180.00, 'alps.jpg'),
(3, 'Kyoto', 'Japan', 'Ancient imperial capital famous for classical Buddhist temples, gardens, and shrines.', 'Culture', 110.00, 'kyoto.jpg'),
(4, 'Paris', 'France', 'World capital of art, fashion, gastronomy, and culture with iconic monuments.', 'Luxury', 160.00, 'paris.jpg'),
(5, 'Kullu-Manali', 'India', 'Picturesque Himalayan valley famous for snow sports, pine forests, Beas river, and Rohtang Pass.', 'Nature', 45.00, 'manali.jpg'),
(6, 'Goa', 'India', 'Vibrant coastal state known for pristine beaches, Portuguese heritage, and nightlife.', 'Beach', 45.00, 'goa.jpg'),
(7, 'Shimla', 'India', 'Queen of Hills famous for colonial architecture, pine forests, Mall Road, and toy train rides.', 'Adventure', 50.00, 'shimla.jpg'),
(8, 'Kerala', 'India', 'God\'s Own Country renowned for tranquil backwaters, tea gardens of Munnar, and Ayurvedic wellness.', 'Nature', 55.00, 'kerala.jpg'),
(9, 'Vietnam', 'Vietnam', 'Breathtaking limestone karst islands of Ha Long Bay, vibrant street food, and imperial heritage.', 'Adventure', 40.00, 'vietnam.jpg'),
(10, 'Maldives', 'Maldives', 'Iconic tropical paradise of private overwater villas, crystal lagoons, and vibrant coral reefs.', 'Luxury', 220.00, 'maldives.jpg'),
(11, 'Egypt', 'Egypt', 'Cradle of ancient civilization boasting the Great Pyramids of Giza, the Sphinx, and grand Nile temples.', 'Culture', 70.00, 'egypt.jpg')
ON DUPLICATE KEY UPDATE name=VALUES(name), country=VALUES(country), description=VALUES(description), category=VALUES(category), estimated_daily_cost=VALUES(estimated_daily_cost), image_url=VALUES(image_url);

-- 3. Travel Packages
INSERT INTO travel_packages (package_id, destination_id, title, description, duration_days, base_price, season, discount_percentage) VALUES
(1, 1, 'Bali Tropical Island Escape', 'Relax on the sands of Kuta, explore Ubud monkey forest, and visit sacred water temples.', 5, 450.00, 'Summer', 10.00),
(2, 2, 'Swiss Alpine Explorer', 'Ride scenic mountain railways, hike the Matterhorn trails, and cruise Lake Geneva.', 6, 1200.00, 'Winter', 5.00),
(3, 3, 'Kyoto Heritage & Zen Trails', 'Experience traditional tea ceremonies, visit the Fushimi Inari shrine and bamboo groves.', 4, 650.00, 'Spring', 0.00),
(4, 4, 'Romantic Paris Luxury Tour', 'Cruise the River Seine, private tour of the Louvre, and dinner at the Eiffel Tower.', 5, 1100.00, 'All Season', 15.00),
(5, 5, 'Kullu-Manali Himalayan Adventure', 'Paragliding in Solang Valley, river rafting in Kullu, and scenic treks through pine forests.', 4, 240.00, 'Summer', 10.00),
(6, 6, 'Goan Coastal & Sunset Retreat', 'Beach hopping in North & South Goa, water sports, and historical church tours.', 3, 180.00, 'Winter', 10.00),
(7, 7, 'Shimla Colonial Hills & Pine Trails', 'Ride the heritage toy train, explore Kufri snow viewpoint, and stroll along historic Ridge.', 4, 280.00, 'All Season', 5.00),
(8, 8, 'Kerala Backwaters & Munnar Tea Mist', 'Private houseboat cruise on Alleppey backwaters, misty Munnar tea plantations, and spice walks.', 5, 380.00, 'Winter', 10.00),
(9, 9, 'Vietnam Ha Long Bay & Hanoi Discovery', 'Explore Hanoi old quarter, overnight cruise on Ha Long Bay, and cave kayaking in Tam Coc.', 5, 420.00, 'All Season', 15.00),
(10, 10, 'Maldives Luxury Overwater Paradise', 'Overwater villa retreat, turquoise lagoon swimming, house reef snorkeling, and sandbank dinner.', 4, 1250.00, 'Winter', 12.00),
(11, 11, 'Ancient Egypt Pyramids & Nile Odyssey', 'Marvel at Giza Pyramids, visit the Grand Egyptian Museum, and sail to ancient Luxor temples.', 6, 690.00, 'Winter', 10.00)
ON DUPLICATE KEY UPDATE destination_id=VALUES(destination_id), title=VALUES(title), description=VALUES(description), duration_days=VALUES(duration_days), base_price=VALUES(base_price), season=VALUES(season), discount_percentage=VALUES(discount_percentage);

-- 4. Sample Itinerary Days for Package 1 (Bali)
INSERT INTO itinerary_days (package_id, day_number, title, activity_description) VALUES
(1, 1, 'Arrival & Hotel Check-in', 'Arrive at Ngurah Rai Airport, private transfer to hotel, and sunset walk along Kuta Beach.'),
(1, 2, 'Ubud Cultural Exploration', 'Visit the Sacred Monkey Forest Sanctuary, Ubud Art Market, and Tegallalang Rice Terraces.'),
(1, 3, 'Volcano View & Coffee Plantation', 'Excursion to Kintamani overlooking Mount Batur followed by Luwak coffee tasting.'),
(1, 4, 'Water Temples & Beach Club', 'Tour Ulun Danu Beratan Temple and evening dinner at Seminyak Beach club.'),
(1, 5, 'Souvenirs & Departure', 'Morning leisure shopping at local craft markets and airport drop-off.');

-- Sample Itinerary Days for Package 5 (Kullu-Manali)
INSERT INTO itinerary_days (package_id, day_number, title, activity_description) VALUES
(5, 1, 'Arrival in Manali', 'Check in to mountain resort, visit Hadimba Devi Temple, and evening stroll at Mall Road.'),
(5, 2, 'Solang Valley Adventures', 'Enjoy paragliding, zorbing, and cable car rides in Solang Valley.'),
(5, 3, 'Atal Tunnel & Sissu Excursion', 'Scenic drive through Atal Tunnel to the breathtaking waterfalls of Lahaul Valley.'),
(5, 4, 'Old Manali & Departure', 'Visit Old Manali cafes, river crossing activity, and departure in the afternoon.');

-- Sample Itinerary Days for Package 7 (Shimla)
INSERT INTO itinerary_days (package_id, day_number, title, activity_description) VALUES
(7, 1, 'Arrival in Shimla & Ridge Walk', 'Arrive in Shimla, check-in to heritage lodge, and stroll along Mall Road and The Ridge.'),
(7, 2, 'Kufri Snow Point Excursion', 'Visit Kufri valley, enjoy scenic horse rides, and view snow-capped Himalayan peaks.'),
(7, 3, 'Heritage Toy Train & Jakhoo Temple', 'Ride the UNESCO heritage Kalka-Shimla toy train and hike to panoramic Jakhoo Temple.'),
(7, 4, 'Lakkar Bazaar & Departure', 'Morning wooden handicraft shopping at Lakkar Bazaar and afternoon departure.');

-- Sample Itinerary Days for Package 8 (Kerala)
INSERT INTO itinerary_days (package_id, day_number, title, activity_description) VALUES
(8, 1, 'Cochin Arrival & Drive to Munnar', 'Airport pickup, scenic mountain drive past Cheeyappara Waterfalls to Munnar tea hills.'),
(8, 2, 'Munnar Tea Gardens & Eravikulam', 'Guided tea factory walk, tasting session, and rare Nilgiri Tahr spotting at Eravikulam.'),
(8, 3, 'Spice Plantations of Thekkady', 'Periyar spice garden tour, wildlife lake boat ride, and traditional Kathakali performance.'),
(8, 4, 'Alleppey Deluxe Houseboat Cruise', 'Board private luxury houseboat, cruise tranquil backwaters, and savor freshly prepared Kerala cuisine.'),
(8, 5, 'Marari Beach & Departure', 'Morning coastal coconut grove walk and transfer to Cochin International Airport.');

-- Sample Itinerary Days for Package 9 (Vietnam)
INSERT INTO itinerary_days (package_id, day_number, title, activity_description) VALUES
(9, 1, 'Hanoi Old Quarter & Street Food', 'Cyclo ride through 36 historic guild streets, Hoan Kiem Lake, and authentic egg coffee tasting.'),
(9, 2, 'Ha Long Bay Overnight Cruise', 'Transfer to coastal marina, board boutique cruise, kayak through karst islands, and sunset dining.'),
(9, 3, 'Sung Sot Cave & Ninh Binh', 'Explore Sung Sot cave chambers, return to mainland, and drive to scenic Tam Coc riverways.'),
(9, 4, 'Trang An Boat Tour & Ancient Capital', 'Bamboo rowing boat excursion through water caves and historic Hoa Lu ancient capital temples.'),
(9, 5, 'Temple of Literature & Departure', 'Morning visit to 11th-century Temple of Literature and transfer to Noi Bai Airport.');

-- Sample Itinerary Days for Package 10 (Maldives)
INSERT INTO itinerary_days (package_id, day_number, title, activity_description) VALUES
(10, 1, 'Speedboat to Island Resort', 'Scenic speedboat transfer across turquoise waters to 5-star private island water villa.'),
(10, 2, 'House Reef Snorkel & Dolphin Cruise', 'Swim with marine turtles along coral walls, followed by sunset spinner dolphin cruise.'),
(10, 3, 'Overwater Spa & Sandbank Dinner', 'Relaxing oceanfront spa treatment and private candlelit dinner on secluded desert sandbank.'),
(10, 4, 'Lagoon Paddle-boarding & Departure', 'Morning clear-water kayaking, souvenir boutique shopping, and transfer to Male.');

-- Sample Itinerary Days for Package 11 (Egypt)
INSERT INTO itinerary_days (package_id, day_number, title, activity_description) VALUES
(11, 1, 'Cairo Arrival & Giza Plateau', 'Check-in to historic Mena House hotel with panoramic views of the Giza Pyramids.'),
(11, 2, 'Pyramids of Giza & The Sphinx', 'Private guided tour of the Great Pyramid of Khufu, the Sphinx, and camel safari.'),
(11, 3, 'Grand Egyptian Museum & Bazaar', 'Visit Tutankhamun golden collection and wander through the labyrinthine Khan el-Khalili bazaar.'),
(11, 4, 'Luxor Flight & Valley of the Kings', 'Short flight to Luxor, visit royal tomb chambers in Valley of the Kings, and Hatshepsut Temple.'),
(11, 5, 'Karnak & Luxor Monumental Temples', 'Walk through the 134 towering stone columns of Karnak and floodlit Luxor Temple at dusk.'),
(11, 6, 'Nile Felucca Sail & Return Flight', 'Traditional wooden felucca sail on the Nile River and departure transfer.');

-- 5. Customer Reviews (Authentic initial ratings for travel packages and destinations)
INSERT INTO reviews (user_id, package_id, rating, comment, review_date) VALUES
(2, 1, 5, 'Breathtaking beaches in Kuta and stunning temples in Ubud. A truly magical vacation!', '2026-10-10 14:30:00'),
(6, 1, 4, 'Very relaxing getaway, loved the Luwak coffee plantation and Mount Batur view.', '2026-10-12 11:15:00'),
(6, 2, 5, 'The Matterhorn mountain trails and scenic glacier trains were magnificent!', '2026-10-14 09:20:00'),
(2, 3, 5, 'Fushimi Inari torii gates and traditional tea ceremonies were unforgettable.', '2026-10-15 16:45:00'),
(6, 4, 5, 'Romantic evening cruise along the Seine and private Louvre tour. Exceptional!', '2026-10-16 20:00:00'),
(2, 4, 4, 'Paris was beautiful. The Eiffel tower dinner was the highlight.', '2026-10-17 18:30:00'),
(2, 5, 4, 'Solang valley paragliding was exhilarating! Crisp mountain air and river rafting.', '2026-10-18 10:10:00'),
(6, 6, 5, 'Beautiful coastal sunsets, delicious local seafood and vibrant beach vibes.', '2026-10-19 12:00:00'),
(2, 7, 4, 'Scenic ride on the UNESCO heritage toy train and peaceful walks on Mall Road.', '2026-10-20 15:30:00'),
(6, 8, 5, 'The private houseboat on Alleppey backwaters was paradise! Serene and authentic cuisine.', '2026-10-21 17:00:00'),
(2, 9, 5, 'Ha Long Bay overnight cruise with cave kayaking was surreal and breathtaking.', '2026-10-22 13:40:00'),
(6, 10, 5, 'Pure luxury in the overwater villa. Turquoise lagoons and vibrant house reef turtles.', '2026-10-23 16:20:00'),
(2, 11, 5, 'Standing in front of the Great Pyramids and sailing the Nile was a dream come true.', '2026-10-24 10:00:00')
ON DUPLICATE KEY UPDATE rating=VALUES(rating), comment=VALUES(comment);