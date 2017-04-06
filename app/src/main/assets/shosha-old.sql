BEGIN TRANSACTION;
CREATE TABLE `usuario` (
	`id`	INTEGER,
	`nombre`	TEXT NOT NULL,
	`email`	TEXT NOT NULL UNIQUE,
	`modificacion` REAL,
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
	`id`	INTEGER NOT NULL,
	`nombre`	TEXT NOT NULL,
	`propietario`	INTEGER NOT NULL,
	`estado`	TEXT NOT NULL,
	PRIMARY KEY(`id`),
	FOREIGN KEY(`propietario`) REFERENCES usuario(id)
);
CREATE TABLE `item` (
	`id`	INTEGER NOT NULL,
	`nombre`	TEXT NOT NULL,
	`precio`	REAL NOT NULL,
	`idLista`	INTEGER NOT NULL,
	PRIMARY KEY(`id`),
	FOREIGN KEY(`idLista`) REFERENCES lista(id)
);
COMMIT;