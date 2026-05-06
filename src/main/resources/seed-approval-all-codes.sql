USE e_medical_booking;

INSERT IGNORE INTO all_codes (key_map, type, value_en, value_vi) VALUES
('AR1', 'APPROVAL_REQUEST_STATUS', 'Pending', 'Chờ duyệt'),
('AR2', 'APPROVAL_REQUEST_STATUS', 'Approved', 'Đã duyệt'),
('AR3', 'APPROVAL_REQUEST_STATUS', 'Rejected', 'Từ chối'),
('AR4', 'APPROVAL_REQUEST_STATUS', 'Cancelled', 'Đã hủy');
