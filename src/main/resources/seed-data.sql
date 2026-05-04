-- ============================================================
-- SEED DATA cho database e_medical_booking
-- Chạy file này trong phpMyAdmin: http://localhost:8080
-- Database: e_medical_booking
-- ============================================================

USE e_medical_booking;

SET FOREIGN_KEY_CHECKS = 0;

-- ============================================================
-- BẢNG: all_codes
-- Chứa các loại mã: ROLE, STATUS, TIME, POSITION, GENDER,
--                   PRICE, PAYMENT, PROVINCE
-- ============================================================

INSERT IGNORE INTO all_codes (key_map, type, value_en, value_vi) VALUES
-- ROLE
('R1',   'ROLE',     'Admin',                    'Quản trị viên'),
('R2',   'ROLE',     'Doctor',                   'Bác sĩ'),
('R3',   'ROLE',     'Patient',                  'Bệnh nhân'),
('R4',   'ROLE',     'Clinic Manager',           'Quản lý phòng khám'),
-- STATUS booking
('S1',   'STATUS',   'New',                      'Lịch hẹn mới'),
('S2',   'STATUS',   'Confirmed',                'Đã xác nhận'),
('S3',   'STATUS',   'Done',                     'Đã khám xong'),
('S4',   'STATUS',   'Cancel',                   'Đã hủy'),
-- TIME slot
('T1',   'TIME',     '8:00 AM - 9:00 AM',        '8:00 - 9:00'),
('T2',   'TIME',     '9:00 AM - 10:00 AM',       '9:00 - 10:00'),
('T3',   'TIME',     '10:00 AM - 11:00 AM',      '10:00 - 11:00'),
('T4',   'TIME',     '11:00 AM - 12:00 PM',      '11:00 - 12:00'),
('T5',   'TIME',     '1:00 PM - 2:00 PM',        '13:00 - 14:00'),
('T6',   'TIME',     '2:00 PM - 3:00 PM',        '14:00 - 15:00'),
('T7',   'TIME',     '3:00 PM - 4:00 PM',        '15:00 - 16:00'),
('T8',   'TIME',     '4:00 PM - 5:00 PM',        '16:00 - 17:00'),
-- POSITION (học vị bác sĩ)
('P0',   'POSITION', 'Doctor',                   'Bác sĩ'),
('P1',   'POSITION', 'Master',                   'Thạc sĩ'),
('P2',   'POSITION', 'Ph.D',                     'Tiến sĩ'),
('P3',   'POSITION', 'Associate Professor',      'Phó giáo sư'),
('P4',   'POSITION', 'Professor',                'Giáo sư'),
('P5',   'POSITION', 'Specialist I',             'Bác sĩ chuyên khoa I'),
('P6',   'POSITION', 'Specialist II',            'Bác sĩ chuyên khoa II'),
-- GENDER
('M',    'GENDER',   'Male',                     'Nam'),
('F',    'GENDER',   'Female',                   'Nữ'),
('O',    'GENDER',   'Other',                    'Khác'),
-- PRICE (giá khám, value_en = USD, value_vi = VNĐ)
('PRI1', 'PRICE',    '8',                        '200000'),
('PRI2', 'PRICE',    '10',                       '250000'),
('PRI3', 'PRICE',    '12',                       '300000'),
('PRI4', 'PRICE',    '14',                       '350000'),
('PRI5', 'PRICE',    '16',                       '400000'),
('PRI6', 'PRICE',    '18',                       '450000'),
('PRI7', 'PRICE',    '20',                       '500000'),
-- PAYMENT
('PAY1', 'PAYMENT',  'Cash',                     'tiền mặt'),
('PAY2', 'PAYMENT',  'Credit card',              'quẹt thẻ ATM'),
('PAY3', 'PAYMENT',  'All payment method',       'tiền mặt và quẹt thẻ ATM'),
-- PROVINCE
('PRO1',  'PROVINCE', 'Ha Noi',                  'Hà Nội'),
('PRO2',  'PROVINCE', 'Ho Chi Minh City',        'Hồ Chí Minh'),
('PRO3',  'PROVINCE', 'Da Nang',                 'Đà Nẵng'),
('PRO4',  'PROVINCE', 'Can Tho',                 'Cần Thơ'),
('PRO5',  'PROVINCE', 'Binh Duong',              'Bình Dương'),
('PRO6',  'PROVINCE', 'Dong Nai',                'Đồng Nai'),
('PRO7',  'PROVINCE', 'Quang Ninh',              'Quảng Ninh'),
('PRO8',  'PROVINCE', 'Hue',                     'Thừa Thiên Huế'),
('PRO9',  'PROVINCE', 'Quang Binh',              'Quảng Bình'),
('PRO10', 'PROVINCE', 'Khanh Hoa',               'Khánh Hòa'),
-- PACKAGE (loại gói khám)
('PK1',  'PACKAGE',  'Basic',                    'Cơ bản'),
('PK2',  'PACKAGE',  'VIP',                      'VIP'),
('PK3',  'PACKAGE',  'Advanced',                 'Nâng cao'),
('PK4',  'PACKAGE',  'Pre-Marital',              'Tiền Hôn Nhân'),
('PK5',  'PACKAGE',  'Women\'s Health',          'Nữ'),
('PK6',  'PACKAGE',  'Cancer Screening',         'Tầm Soát Ung Thư'),
('PK7',  'PACKAGE',  'General Disease',          'Bệnh Lý Chung'),
-- GROUP_SERVICE (nhóm dịch vụ trong gói khám)
('GS1',  'GROUP_SERVICE', 'Clinical Examination',                        'Khám lâm sàng'),
('GS2',  'GROUP_SERVICE', 'Laboratory Tests',                            'Xét nghiệm'),
('GS3',  'GROUP_SERVICE', 'Imaging & Functional Diagnostics',            'Chẩn đoán hình ảnh và thăm dò chức năng'),
('GS4',  'GROUP_SERVICE', 'Result Consultation',                         'Tư vấn kết quả');

-- ============================================================
-- KIỂM TRA KẾT QUẢ
-- ============================================================
SELECT type, COUNT(*) as total FROM all_codes GROUP BY type ORDER BY type;

SET FOREIGN_KEY_CHECKS = 1;
