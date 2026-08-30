Promt utilizado

Primero debe pedir la cantidad de vehículos que se van a ingresar.
Después, por cada vehículo le debe pedir: nombre del cliente, placa,
tipo de vehículo (Moto, Auto o Camioneta), horas de
estacionamiento, y si es cliente frecuente o no.

Reglas de las tarifas:
- Moto: 2 x hora
- Auto: 4 x hora
- Camioneta: 10 x hora

Reglas de los recargos:
- Si son 2 horas debe pagar la tarifa normal
- Si son 3 o 4 horas debe pagar 20% más por cada hora
- Si son 5 horas o más debe pagar 50% más por cada hora
- Si el cliente es frecuente se le descuenta el 10% del total
  final de su vehículo
- Ningún vehículo se puede registrar con solo 1 hora, mínimo debe ser 2.

La salida debe verse así:
Tarifa basica "nombre cliente" - "placa"
hora     tarifa     recargo     importe
1        4.00       0%          4.00
2        4.00       0%          4.00
3        4.00       20%         4.80
total:      12.80 soles

Al final, después de registrar todos los vehículos debe mostrar un resumen
del día con: cuántos vehículos en total, cuántos eran motos, cuántos
autos y cuántas camionetas, cuántas horas estuvieron en total todos
los vehículos, y la ganancia total del día.

Importante: dame el código en 3 partes para poder hacer 3 commits por
separado: primero el ingreso de datos, después los cálculos, y al
final el resumen. Usa código simple de Kotlin, sin cosas avanzadas como clases
abstractas o herencia.