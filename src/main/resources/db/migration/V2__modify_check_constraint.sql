ALTER TABLE users DROP COLUMN nickname;
ALTER TABLE meeting DROP COLUMN meeting_status;
ALTER TABLE participation
    DROP COLUMN button_authority,
    DROP COLUMN meeting_authority,
    DROP COLUMN participation_meeting_status;

ALTER TABLE meeting
    ADD COLUMN meeting_status VARCHAR(255) CHECK (meeting_status IN ('SCHEDULED', 'CONFIRMATION', 'ONGOING', 'TERMINATION'));

ALTER TABLE participation
    ADD COLUMN button_authority VARCHAR(255) CHECK (button_authority IN ('OWNER', 'NON_OWNER')),
    ADD COLUMN meeting_authority VARCHAR(255) CHECK (meeting_authority IN ('HOST', 'PARTICIPATION')),
    ADD COLUMN participation_meeting_status VARCHAR(255) CHECK (participation_meeting_status IN ('PARTICIPATING', 'DELETE'));

ALTER TABLE users
    ADD COLUMN nickname VARCHAR(255),
    ADD COLUMN platform_id VARCHAR(255),
    ADD COLUMN refresh_token VARCHAR(255),
    ADD COLUMN platform VARCHAR(255) CHECK (platform IN ('KAKAO', 'APPLE', 'WITHDRAW'));