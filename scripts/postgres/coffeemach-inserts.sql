/*Clean Data (Sin Where)*/
delete from ingrediente;
delete from receta;
delete from receta_ingrediente;
delete from alarma;
delete from maquina;
delete from alarma_maquina;
delete from operadores;
delete from asignacion_maquina;

/*Ingredientes*/
insert into ingrediente (idingrediente,nombre) values (1,'Agua');
insert into ingrediente (idingrediente,nombre) values (2,'Cafe');
insert into ingrediente (idingrediente,nombre) values (3,'Azucar');
insert into ingrediente (idingrediente,nombre) values (4,'Vaso');

/*Recetas*/
insert into receta (idreceta,nombre,precio) values (1,'Tinto',800);
insert into receta (idreceta,nombre,precio) values (2,'Tinto Cargado', 1000);

/*Receta Ingrediente*/
insert into receta_ingrediente (idreceta,idingrediente,unidades) values (1,1,100);
insert into receta_ingrediente (idreceta,idingrediente,unidades) values (1,2,10);
insert into receta_ingrediente (idreceta,idingrediente,unidades) values (1,3,10);
insert into receta_ingrediente (idreceta,idingrediente,unidades) values (1,4,1);
insert into receta_ingrediente (idreceta,idingrediente,unidades) values (2,1,100);
insert into receta_ingrediente (idreceta,idingrediente,unidades) values (2,2,30);
insert into receta_ingrediente (idreceta,idingrediente,unidades) values (2,3,10);
insert into receta_ingrediente (idreceta,idingrediente,unidades) values (2,4,1);

/*Alarmas*/
insert into alarma (idalarma,nombre) values (1,'alarma1');
insert into alarma (idalarma,nombre) values (2,'alarma2');
insert into alarma (idalarma,nombre) values (3,'alarma3');
insert into alarma (idalarma,nombre) values (4,'alarma4');
insert into alarma (idalarma,nombre) values (5,'alarma5');
insert into alarma (idalarma,nombre) values (6,'alarma6');
insert into alarma (idalarma,nombre) values (7,'alarma7');
insert into alarma (idalarma,nombre) values (8,'alarma8');
insert into alarma (idalarma,nombre) values (9,'alarma9');
insert into alarma (idalarma,nombre) values (10,'alarma10');
insert into alarma (idalarma,nombre) values (11,'alarma11');
insert into alarma (idalarma,nombre) values (12,'alarma12');

/*Máquinas*/
insert into maquina (idmaquina,ubicacion) values (1,'U.Icesi-Edificio C');
insert into maquina (idmaquina,ubicacion) values (2,'U.Icesi-Edificio E');
insert into maquina (idmaquina,ubicacion) values (3,'Av 5. N 33-34');
insert into maquina (idmaquina,ubicacion) values (4,'Cra 12 N 66-90');
insert into maquina (idmaquina,ubicacion) values (5,'Cosmocentro');
insert into maquina (idmaquina,ubicacion) values (6,'Chipichape');
insert into maquina (idmaquina,ubicacion) values (7,'Palmeto');
insert into maquina (idmaquina,ubicacion) values (8,'Jardin Plaza');

/*Alarma Máquina*/
insert into alarma_maquina (id_alarma,id_maquina,fecha_inicial,fecha_final,consecutivo) values (1,1,now(),null,1);
insert into alarma_maquina (id_alarma,id_maquina,fecha_inicial,fecha_final,consecutivo) values (2,5,now(),null,2);
insert into alarma_maquina (id_alarma,id_maquina,fecha_inicial,fecha_final,consecutivo) values (3,1,now(),null,3);
insert into alarma_maquina (id_alarma,id_maquina,fecha_inicial,fecha_final,consecutivo) values (4,3,now(),null,4);
insert into alarma_maquina (id_alarma,id_maquina,fecha_inicial,fecha_final,consecutivo) values (1,1,now(),null,5);

/*Operadores*/
insert into operadores (idoperador,nombre,correo,contrasena) values (1,'Miguel Angel','test@gmail.com','1123');
insert into operadores (idoperador,nombre,correo,contrasena) values (2,'Donatello','test1@gmail.com','2123');
insert into operadores (idoperador,nombre,correo,contrasena) values (3,'Raffaello','test2@gmail.com','3123');
insert into operadores (idoperador,nombre,correo,contrasena) values (4,'Leonardo','test3@gmail.com','4123');

/*Asignacion Máquina*/
insert into asignacion_maquina (id_operador,id_maquina) values (1,1);
insert into asignacion_maquina (id_operador,id_maquina) values (1,2);
insert into asignacion_maquina (id_operador,id_maquina) values (1,3);
insert into asignacion_maquina (id_operador,id_maquina) values (2,4);
insert into asignacion_maquina (id_operador,id_maquina) values (2,5);
insert into asignacion_maquina (id_operador,id_maquina) values (2,6);
insert into asignacion_maquina (id_operador,id_maquina) values (3,7);
insert into asignacion_maquina (id_operador,id_maquina) values (4,8);

commit;
