# TrackMed

Sistema de registro de proximidade de um agente em relação a pontos de interesse de monitoramento em ambiente hospitalar. Usando beacons para identificar os pontos a serem monitorados dentro de um hospital, é possível estimar a proximidade de médicos, enfermeiros, prestadores de serviços etc. em relação a esses locais como leitos, dispensers de líquidos higienizantes, bebedouros, equipamentos médicos, portas ou salas.

Além da observação do padrão de deslocamento, esses dados podem ser utilizados para colaborar com os protocolos de controle de infecção ou na tentativa de reconstruir inversamente a disseminação de algum patógeno dentro do ambiente hospitalar.

## *Beacons*

Para que os *beacons* sejam reconhecidos como rastreáveis pelo aplicativo TrackMed a identificação da instância ou **Instance Id** deve começar com o hexadecimal **AA**.

As duas posições seguintes identificam o tipo de objeto que o *beacon* está rastreando. A tabela abaixo contém todos os identificadores possíveis:

|Identificador|Tipo de objeto|
|-------------|--------------|
|00           |Leito         |
|01           |Bebedouro     |
|02           |*Dispenser*   |
|03           |Recepção		 |
|04           |Máquina de vendas|
|05           |Computador	 |
|06           |Armário		 |
|07           |Vestiário	 |
|08           |Quadro de avisos|
|09           |Relógio de ponto|
|0A           |Máquina 1	 |
|0B           |Máquina 2	 |
|0B           |Máquina 3	 |

> **Exemplo:** AA0500000000 - *Beacon* rastreável pelo TrackMed identificando um computador
