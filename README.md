# wheretolive api

**Clone this project with referenced [git submodules](https://git-scm.com/book/en/v2/Git-Tools-Submodules)**

```bash
git clone git@github.com:DataToKnowledge/wheretolive-api.git
cd wheretolive-api
```

**Init dependent projects**

```bash
./init_subprojects.sh
```

## Development Setup

### 1. Starts the dev env
There is dev env with all the services need by the api, that is, elasticsearch, kafka and zookeeper. Actually,
kafka and zookeeper are not necessary, but we preferred keep a unique dev-env. **It requires the latest version of
docker engine and docker compose!!!**

```bash
./dev start
```

The complete usage of `dev.sh` can be read by running the command `./dev.sh` which outputs:

```bash
Usage: ./dev.sh start | ps | stop | restart | rm | logs

    start --> starts elasticsearch, zookeeper, kafka and trifecta
    ps --> lists the istances running
    rm --> stops and removes all the running instances
    stop --> stops the instances
    restart --> restarts all the dockers
    logs --> starts the log
Example:
$: ./dev.sh start

```

### 2. Start the project with [sbt-revolver](https://github.com/spray/sbt-revolver)

Sbt-Revolver is a sbt plugin that enables to run api via sbt, updates the code when edited and keep the service up.
It must be used only for dev and not in production!!!.

- to start the api
    ```bash

    sbt
    > ~reStart
    ```
- to stop the api, press enter
    ```bash

    > ~reStop
    ```

## Production Setup

Using [sbt-native-packager](https://github.com/sbt/sbt-native-packager) the project can be packaged in a docker container, pushed on docker-hub and pulled from other vm.
The project is already configured, please read the official documentation if you want to change something.
Otherwise, run the following command:

```bash
docker rm -f wtl-api && docker run -dt --name wtl-api --restart on-failure data2knowledge/wheretolive-api:0.6.2
```
