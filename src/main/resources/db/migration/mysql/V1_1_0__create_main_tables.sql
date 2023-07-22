-- player definition
CREATE TABLE player
(
    id                int NOT NULL AUTO_INCREMENT,
    player_discord_id varchar(255) DEFAULT NULL,
    player_id         varchar(36)  DEFAULT NULL,
    player_name       varchar(255) DEFAULT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ticket definition
CREATE TABLE ticket
(
    id               int NOT NULL AUTO_INCREMENT,
    penalty_amount   int          DEFAULT NULL,
    reason           varchar(256) DEFAULT NULL,
    should_be_paid   bit(1)       DEFAULT NULL,
    ticket_type      varchar(255) DEFAULT NULL,
    police_player_id int          DEFAULT NULL,
    source_ticket_id int          DEFAULT NULL,
    target_player_id int          DEFAULT NULL,
    victim_player_id int          DEFAULT NULL,
    PRIMARY KEY (id),
    KEY              index_target_player_id (target_player_id),
    KEY              index_victim_player_id (victim_player_id),
    KEY              ticket_FK (police_player_id),
    KEY              ticket_FK_3 (source_ticket_id),
    CONSTRAINT ticket_FK FOREIGN KEY (police_player_id) REFERENCES player (id),
    CONSTRAINT ticket_FK_1 FOREIGN KEY (target_player_id) REFERENCES player (id),
    CONSTRAINT ticket_FK_2 FOREIGN KEY (victim_player_id) REFERENCES player (id),
    CONSTRAINT ticket_FK_3 FOREIGN KEY (source_ticket_id) REFERENCES ticket (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- schedule definition
CREATE TABLE schedule
(
    id               int NOT NULL AUTO_INCREMENT,
    is_active        bit(1)   DEFAULT NULL,
    bukkit_task_id   int      DEFAULT NULL,
    deadline         datetime DEFAULT NULL,
    source_ticket_id int      DEFAULT NULL,
    PRIMARY KEY (id),
    KEY              index_source_ticket_id (source_ticket_id),
    KEY              index_is_active (is_active),
    KEY              index_deadline (deadline),
    CONSTRAINT schedule_FK FOREIGN KEY (source_ticket_id) REFERENCES ticket (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;