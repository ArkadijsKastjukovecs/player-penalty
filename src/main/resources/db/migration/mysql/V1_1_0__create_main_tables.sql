-- player definition
CREATE TABLE player
(
    id                INTEGER PRIMARY KEY AUTOINCREMENT,
    player_id         varchar(255),
    player_discord_id varchar(255),
    player_name       varchar(255)
);

-- ticket definition
CREATE TABLE ticket
(
    id               INTEGER PRIMARY KEY AUTOINCREMENT,
    penalty_amount   INTEGER,
    reason           VARCHAR(256),
    ticket_type      VARCHAR(255),
    police_player_id INTEGER,
    source_ticket_id INTEGER,
    target_player_id INTEGER,
    victim_player_id INTEGER,
    should_be_paid   BOOLEAN,
    CONSTRAINT ticket_FK FOREIGN KEY (source_ticket_id) REFERENCES ticket (id),
    CONSTRAINT ticket_FK_1 FOREIGN KEY (police_player_id) REFERENCES player (player_id),
    CONSTRAINT ticket_FK_2 FOREIGN KEY (target_player_id) REFERENCES player (player_id),
    CONSTRAINT ticket_FK_3 FOREIGN KEY (victim_player_id) REFERENCES player (player_id)
);

-- schedule definition
CREATE TABLE schedule
(
    id               INTEGER PRIMARY KEY AUTOINCREMENT,
    is_active        BOOLEAN,
    deadline         DATETIME,
    source_ticket_id INTEGER,
    bukkit_task_id   INTEGER,
    CONSTRAINT schedule_FK FOREIGN KEY (source_ticket_id) REFERENCES ticket (id)
);