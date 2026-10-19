
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
(1, 'System Administrator', 'admin@tourism.com', '$2a$10$wK1F5wEw3Xf0nI/b3D8QZOHk4V5d7L7R5oA1Z7s5W2v3n4K5e6X7.', 'ADMIN', '9998887770'),
(2, 'John Doe', 'john@gmail.com', '$2a$10$tZ8J0m2V4Q5P6L7W8E9R1u3Y5T7I9O1P3S5D7F9G1H3J5K7L9Z1X.', 'CUSTOMER', '9876543210')
ON DUPLICATE KEY UPDATE user_id=user_id;

-- 2. Destinations
INSERT INTO destinations (destination_id, name, country, description, category, estimated_daily_cost, image_url) VALUES
(1, 'Bali', 'Indonesia', 'Tropical paradise known for iconic beaches, volcanic mountains, and coral reefs.', 'Beach', 60.00, 'bali.jpg'),
(2, 'Swiss Alps', 'Switzerland', 'Magnificent alpine region perfect for hiking, skiing, and breathtaking scenic trains.', 'Adventure', 180.00, 'alps.jpg'),
(3, 'Kyoto', 'Japan', 'Ancient imperial capital famous for classical Buddhist temples, gardens, and shrines.', 'Culture', 110.00, 'kyoto.jpg'),
(4, 'Paris', 'France', 'World capital of art, fashion, gastronomy, and culture with iconic monuments.', 'Luxury', 160.00, 'paris.jpg'),
(5, 'Manali', 'India', 'High-altitude Himalayan resort town known for snow sports, pine forests, and rivers.', 'Nature', 40.00, 'manali.jpg'),
(6, 'Goa', 'India', 'Vibrant coastal state known for pristine beaches, Portuguese heritage, and nightlife.', 'Beach', 45.00, 'goa.jpg')
ON DUPLICATE KEY UPDATE destination_id=destination_id;

-- 3. Travel Packages
INSERT INTO travel_packages (package_id, destination_id, title, description, duration_days, base_price, season, discount_percentage) VALUES
(1, 1, 'Bali Tropical Island Escape', 'Relax on the sands of Kuta, explore Ubud monkey forest, and visit sacred water temples.', 5, 450.00, 'Summer', 10.00),
(2, 2, 'Swiss Alpine Explorer', 'Ride scenic mountain railways, hike the Matterhorn trails, and cruise Lake Geneva.', 6, 1200.00, 'Winter', 5.00),
(3, 3, 'Kyoto Heritage & Zen Trails', 'Experience traditional tea ceremonies, visit the Fushimi Inari shrine and bamboo groves.', 4, 650.00, 'Spring', 0.00),
(4, 4, 'Romantic Paris Luxury Tour', 'Cruise the River Seine, private tour of the Louvre, and dinner at the Eiffel Tower.', 5, 1100.00, 'All Season', 15.00),
(5, 5, 'Himalayan Adventure in Manali', 'Solang Valley paragliding, river rafting, and trekking through pine valleys.', 4, 220.00, 'Summer', 0.00),
(6, 6, 'Goan Coastal & Sunset Retreat', 'Beach hopping in North & South Goa, water sports, and historical church tours.', 3, 180.00, 'Winter', 10.00)
ON DUPLICATE KEY UPDATE package_id=package_id;

-- 4. Sample Itinerary Days for Package 1 (Bali)
INSERT INTO itinerary_days (package_id, day_number, title, activity_description) VALUES
(1, 1, 'Arrival & Hotel Check-in', 'Arrive at Ngurah Rai Airport, private transfer to hotel, and sunset walk along Kuta Beach.'),
(1, 2, 'Ubud Cultural Exploration', 'Visit the Sacred Monkey Forest Sanctuary, Ubud Art Market, and Tegallalang Rice Terraces.'),
(1, 3, 'Volcano View & Coffee Plantation', 'Excursion to Kintamani overlooking Mount Batur followed by Luwak coffee tasting.'),
(1, 4, 'Water Temples & Beach Club', 'Tour Ulun Danu Beratan Temple and evening dinner at Seminyak Beach club.'),
(1, 5, 'Souvenirs & Departure', 'Morning leisure shopping at local craft markets and airport drop-off.');

-- Sample Itinerary Days for Package 5 (Manali)
INSERT INTO itinerary_days (package_id, day_number, title, activity_description) VALUES
(5, 1, 'Arrival in Manali', 'Check in to mountain resort, visit Hadimba Devi Temple, and evening stroll at Mall Road.'),
(5, 2, 'Solang Valley Adventures', 'Enjoy paragliding, zorbing, and cable car rides in Solang Valley.'),
(5, 3, 'Atal Tunnel & Sissu Excursion', 'Scenic drive through Atal Tunnel to the breathtaking waterfalls of Lahaul Valley.'),
(5, 4, 'Old Manali & Departure', 'Visit Old Manali cafes, river crossing activity, and departure in the afternoon.');