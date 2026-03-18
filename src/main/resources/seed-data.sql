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

INSERT IGNORE INTO all_codes (id, key_map, type, value_en, value_vi) VALUES
-- ROLE
(1,  'R1',   'ROLE',     'Admin',                    'Quản trị viên'),
(2,  'R2',   'ROLE',     'Doctor',                   'Bác sĩ'),
(3,  'R3',   'ROLE',     'Patient',                  'Bệnh nhân'),
-- STATUS booking
(4,  'S1',   'STATUS',   'New',                      'Lịch hẹn mới'),
(5,  'S2',   'STATUS',   'Confirmed',                'Đã xác nhận'),
(6,  'S3',   'STATUS',   'Done',                     'Đã khám xong'),
(7,  'S4',   'STATUS',   'Cancel',                   'Đã hủy'),
-- TIME slot
(8,  'T1',   'TIME',     '8:00 AM - 9:00 AM',        '8:00 - 9:00'),
(9,  'T2',   'TIME',     '9:00 AM - 10:00 AM',       '9:00 - 10:00'),
(10, 'T3',   'TIME',     '10:00 AM - 11:00 AM',      '10:00 - 11:00'),
(11, 'T4',   'TIME',     '11:00 AM - 12:00 PM',      '11:00 - 12:00'),
(12, 'T5',   'TIME',     '1:00 PM - 2:00 PM',        '13:00 - 14:00'),
(13, 'T6',   'TIME',     '2:00 PM - 3:00 PM',        '14:00 - 15:00'),
(14, 'T7',   'TIME',     '3:00 PM - 4:00 PM',        '15:00 - 16:00'),
(15, 'T8',   'TIME',     '4:00 PM - 5:00 PM',        '16:00 - 17:00'),
-- POSITION (học vị bác sĩ)
(16, 'P0',   'POSITION', 'Doctor',                   'Bác sĩ'),
(17, 'P1',   'POSITION', 'Master',                   'Thạc sĩ'),
(18, 'P2',   'POSITION', 'Ph.D',                     'Tiến sĩ'),
(19, 'P3',   'POSITION', 'Associate Professor',      'Phó giáo sư'),
(20, 'P4',   'POSITION', 'Professor',                'Giáo sư'),
(44, 'P5',   'POSITION', 'Specialist I',             'Bác sĩ chuyên khoa I'),
(45, 'P6',   'POSITION', 'Specialist II',            'Bác sĩ chuyên khoa II'),
-- GENDER
(21, 'M',    'GENDER',   'Male',                     'Nam'),
(22, 'F',    'GENDER',   'Female',                   'Nữ'),
(23, 'O',    'GENDER',   'Other',                    'Khác'),
-- PRICE (giá khám, value_en = USD, value_vi = VNĐ)
(24, 'PRI1', 'PRICE',    '8',                        '200000'),
(25, 'PRI2', 'PRICE',    '10',                       '250000'),
(26, 'PRI3', 'PRICE',    '12',                       '300000'),
(27, 'PRI4', 'PRICE',    '14',                       '350000'),
(28, 'PRI5', 'PRICE',    '16',                       '400000'),
(29, 'PRI6', 'PRICE',    '18',                       '450000'),
(30, 'PRI7', 'PRICE',    '20',                       '500000'),
-- PAYMENT
(31, 'PAY1', 'PAYMENT',  'Cash',                     'tiền mặt'),
(32, 'PAY2', 'PAYMENT',  'Credit card',              'quẹt thẻ ATM'),
(33, 'PAY3', 'PAYMENT',  'All payment method',       'tiền mặt và quẹt thẻ ATM'),
-- PROVINCE
(34, 'PRO1',  'PROVINCE', 'Ha Noi',                  'Hà Nội'),
(35, 'PRO2',  'PROVINCE', 'Ho Chi Minh City',        'Hồ Chí Minh'),
(36, 'PRO3',  'PROVINCE', 'Da Nang',                 'Đà Nẵng'),
(37, 'PRO4',  'PROVINCE', 'Can Tho',                 'Cần Thơ'),
(38, 'PRO5',  'PROVINCE', 'Binh Duong',              'Bình Dương'),
(39, 'PRO6',  'PROVINCE', 'Dong Nai',                'Đồng Nai'),
(40, 'PRO7',  'PROVINCE', 'Quang Ninh',              'Quảng Ninh'),
(41, 'PRO8',  'PROVINCE', 'Hue',                     'Thừa Thiên Huế'),
(42, 'PRO9',  'PROVINCE', 'Quang Binh',              'Quảng Bình'),
(43, 'PRO10', 'PROVINCE', 'Khanh Hoa',               'Khánh Hòa'),
-- PACKAGE (loại gói khám)
(46, 'PK1',  'PACKAGE',  'Basic',                    'Cơ bản'),
(47, 'PK2',  'PACKAGE',  'VIP',                      'VIP'),
(48, 'PK3',  'PACKAGE',  'Advanced',                 'Nâng cao'),
(49, 'PK4',  'PACKAGE',  'Pre-Marital',              'Tiền Hôn Nhân'),
(50, 'PK5',  'PACKAGE',  'Women\'s Health',          'Nữ'),
(51, 'PK6',  'PACKAGE',  'Cancer Screening',         'Tầm Soát Ung Thư'),
(52, 'PK7',  'PACKAGE',  'General Disease',          'Bệnh Lý Chung'),
-- GROUP_SERVICE (nhóm dịch vụ trong gói khám)
(53, 'GS1',  'GROUP_SERVICE', 'Clinical Examination',                        'Khám lâm sàng'),
(54, 'GS2',  'GROUP_SERVICE', 'Laboratory Tests',                            'Xét nghiệm'),
(55, 'GS3',  'GROUP_SERVICE', 'Imaging & Functional Diagnostics',            'Chẩn đoán hình ảnh và thăm dò chức năng'),
(56, 'GS4',  'GROUP_SERVICE', 'Result Consultation',                         'Tư vấn kết quả');

-- ============================================================
-- KIỂM TRA KẾT QUẢ
-- ============================================================
SELECT type, COUNT(*) as total FROM all_codes GROUP BY type ORDER BY type;

SET FOREIGN_KEY_CHECKS = 1;
