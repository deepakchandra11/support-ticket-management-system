CREATE TABLE tickets (
    id UUID NOT NULL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description VARCHAR(10000) NOT NULL,
    priority VARCHAR(20) NOT NULL,
    status VARCHAR(30) NOT NULL,
    assignee VARCHAR(200),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    version BIGINT NOT NULL
);

CREATE TABLE comments (
    id UUID NOT NULL PRIMARY KEY,
    ticket_id UUID NOT NULL,
    body VARCHAR(5000) NOT NULL,
    author VARCHAR(200) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_comments_ticket FOREIGN KEY (ticket_id) REFERENCES tickets (id)
);

CREATE INDEX idx_tickets_status ON tickets (status);
CREATE INDEX idx_comments_ticket_id ON comments (ticket_id);
