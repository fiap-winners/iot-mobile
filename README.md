# TrackMed

Sistema de registro de proximidade de um agente em relação a pontos de interesse de monitoramento em ambiente hospitalar. Usando beacons para identificar os pontos a serem monitorados dentro de um hospital, é possível estimar a proximidade de médicos, enfermeiros, prestadores de serviços etc. em relação a esses locais como leitos, *dispensers* de líquidos higienizantes, bebedouros, equipamentos médicos, portas ou salas.

Além da observação do padrão de deslocamento, esses dados podem ser utilizados para colaborar com os protocolos de controle de infecção ou na tentativa de reconstruir inversamente a disseminação de algum patógeno dentro do ambiente hospitalar.

### Para testar o funcionamento

- Instale em um segundo celular o aplicativo [Beacon Simulator](https://play.google.com/store/apps/details?id=net.alea.beaconsimulator)
- Adicione quantos *beacons* desejar, conforme a quantidade e diversidade de objetos que deseja rastrear. Escolha sempre do tipo **Eddystone UID**
- Observe a configuração da identificação dos *beacons* conforme as instruções abaixo
- Ligue um beacon de cada vez, desligando o anterior, simulando o deslocamento de uma pessoa dentro de um ambiente. Nada impede que mais de um *beacon* esteja ligado ao mesmo tempo, pois na vida real o aplicativo irá registrar o mais próximo, mas como todos os *beacons* virtuais estarão na mesma distância pode ser que o mesmo *beacon* não seja detectado durante o tempo mínimo para ser registrado
- Após ligar e desligar albuns *beacons* clique no botão "Histórico" para verificar quais foram registrados

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
|0C           |Máquina 3	 |

> **Exemplo:** AA0500000000 - *Beacon* rastreável pelo TrackMed identificando um computador

## Registro de proximidade

O aplicativo TrackMed exibe o *beacon* mais próximo desde que a distância seja, no máximo, até 10 metros.

O registro de proximidade de um *beacon* é realizado somente se o agente estiver a menos de 2 metros dele por, no mínimo, 2 segundos. Após o registro, se o agente se afastar por mais de 10 metros e se aproximar novamente do mesmo *beacon*, será considerada uma nova aproximação, sendo registrado mais uma vez, mesmo que nenhum outro *beacon* tenha sido detectado nesse período.

> A distância do dispositivo e os *beacons* sofre variação por diversos fatores. Mais informações podem ser obtidas na página da biblioteca [altBeacon](https://altbeacon.github.io/android-beacon-library/distance-calculations.html).
