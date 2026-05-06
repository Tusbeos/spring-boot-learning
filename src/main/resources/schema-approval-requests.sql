-- Schema for approval_requests.
-- Use this manually when Hibernate ddl-auto is validate/none.

CREATE TABLE IF NOT EXISTS approval_requests (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  request_code VARCHAR(50) NOT NULL,
  target_type VARCHAR(50) NOT NULL,
  target_id BIGINT NOT NULL,
  clinic_id BIGINT NULL,
  requester_id BIGINT NULL,
  reviewer_id BIGINT NULL,
  status_id VARCHAR(255) NOT NULL,
  title VARCHAR(255) NOT NULL,
  description TEXT NULL,
  payload_json TEXT NULL,
  review_note TEXT NULL,
  submitted_at DATETIME NOT NULL,
  reviewed_at DATETIME NULL,
  created_at DATETIME NULL,
  updated_at DATETIME NULL,

  CONSTRAINT fk_approval_clinic
    FOREIGN KEY (clinic_id) REFERENCES clinics(id),
  CONSTRAINT fk_approval_requester
    FOREIGN KEY (requester_id) REFERENCES users(id),
  CONSTRAINT fk_approval_reviewer
    FOREIGN KEY (reviewer_id) REFERENCES users(id),
  CONSTRAINT fk_approval_status
    FOREIGN KEY (status_id) REFERENCES all_codes(key_map)
);

CREATE UNIQUE INDEX idx_approval_request_code
  ON approval_requests(request_code);

CREATE INDEX idx_approval_target
  ON approval_requests(target_type, target_id);

CREATE INDEX idx_approval_clinic_status
  ON approval_requests(clinic_id, status_id);

CREATE INDEX idx_approval_requester
  ON approval_requests(requester_id);

CREATE INDEX idx_approval_reviewer
  ON approval_requests(reviewer_id);
