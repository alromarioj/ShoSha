BEGIN TRANSACTION;
CREATE TABLE `usuario` (
	`id`	TEXT,
	`nombre`	TEXT NOT NULL,
	`email`	TEXT NOT NULL UNIQUE,
	PRIMARY KEY(`id`)
);
CREATE TABLE `participa` (
	`idLista`	INTEGER,
	`idUsuario`	INTEGER,
	`activo`	NUMERIC NOT NULL,
	PRIMARY KEY(`idLista`,`idUsuario`),
	FOREIGN KEY(`idLista`) REFERENCES lista(id),
	FOREIGN KEY(`idUsuario`) REFERENCES usuario(id)
);
CREATE TABLE `lista` (
	`id`	TEXT NOT NULL,
	`nombre`	TEXT NOT NULL,
	`propietario`	TEXT NOT NULL,
	`estado`	TEXT NOT NULL,
	PRIMARY KEY(`id`),
	FOREIGN KEY(`propietario`) REFERENCES usuario(id)
);
CREATE TABLE `item` (
	`id`	TEXT NOT NULL,
	`nombre`	TEXT NOT NULL,
	`precio`	REAL NOT NULL,
	`idLista`	TEXT NOT NULL,
	PRIMARY KEY(`id`),
	FOREIGN KEY(`idLista`) REFERENCES lista(id)
);
COMMIT;
