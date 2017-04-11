BEGIN TRANSACTION;
CREATE TABLE "usuario" (
	`id`	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
	`nombre`	varchar(50) NOT NULL,
	`email`	varchar(100) NOT NULL
);
CREATE TABLE `participa` (
	`idLista`	INTEGER NOT NULL,
	`idUsuario`	INTEGER NOT NULL,
	`activo`	tinyint(1) NOT NULL,
	PRIMARY KEY(`idLista`,`idUsuario`),
	FOREIGN KEY(`idLista`) REFERENCES `lista`(`id`) ON DELETE CASCADE ON UPDATE CASCADE,
	FOREIGN KEY(`idUsuario`) REFERENCES `usuario`(`id`) ON DELETE CASCADE ON UPDATE CASCADE
);
CREATE TABLE "lista" (
	`id`	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
	`nombre`	varchar(100) NOT NULL,
	`propietario`	INTEGER NOT NULL,
	`estado`	tinyint(1) NOT NULL,
	FOREIGN KEY(`propietario`) REFERENCES `usuario`(`id`) ON DELETE CASCADE ON UPDATE CASCADE
);
CREATE TABLE "item" (
	`id`	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
	`nombre`	varchar(100) NOT NULL,
	`precio`	double,
	`idLista`	INTEGER NOT NULL,
	`comprado`  INTEGER,
	FOREIGN KEY(`idLista`) REFERENCES `lista`(`id`) ON DELETE CASCADE ON UPDATE CASCADE
);
CREATE TABLE `checksums` (
  `tabla` varchar(50) NOT NULL,
  `crc` double,
  PRIMARY KEY (`tabla`)
);
COMMIT;
