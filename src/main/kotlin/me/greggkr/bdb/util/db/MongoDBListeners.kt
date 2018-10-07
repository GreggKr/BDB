package me.greggkr.bdb.util.db

import com.mongodb.event.*
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit


class MongoServerMonitorListener : ServerMonitorListener {
    private val logger = LoggerFactory.getLogger(this::class.java)
    override fun serverHearbeatStarted(event: ServerHeartbeatStartedEvent) {
        logger.debug("Heartbeat Started.\n" +
                "  Local ID: ${event.connectionId.localValue}\n" +
                "  Server ID: ${event.connectionId.serverValue}\n" +
                "  Server: ${event.connectionId.serverId}"
        )
    }

    override fun serverHeartbeatSucceeded(event: ServerHeartbeatSucceededEvent) {
        logger.debug("Heartbeat Succeeded.\n" +
                "  Local ID: ${event.connectionId.localValue}\n" +
                "  Server ID: ${event.connectionId.serverValue}\n" +
                "  Server: ${event.connectionId.serverId}\n" +
                "  Took: ${event.getElapsedTime(TimeUnit.MILLISECONDS)}ms\n"
        )
    }

    override fun serverHeartbeatFailed(event: ServerHeartbeatFailedEvent) {
        logger.error("Heartbeat Failed.\n" +
                "  Local ID: ${event.connectionId.localValue}\n" +
                "  Server ID: ${event.connectionId.serverValue}\n" +
                "  Server: ${event.connectionId.serverId}\n" +
                "  Took: ${event.getElapsedTime(TimeUnit.MILLISECONDS)}ms\n" +
                "  Response: ${event.throwable}"
        )
    }
}

class MongoClusterListener : ClusterListener {
    private val logger = LoggerFactory.getLogger(this::class.java)
    override fun clusterOpening(event: ClusterOpeningEvent) {
        logger.info("Cluster Opening.\n" +
                "  ID: ${event.clusterId.value}\n" +
                "  Description: ${event.clusterId.description}")
    }

    override fun clusterDescriptionChanged(event: ClusterDescriptionChangedEvent) {
        logger.debug("Cluster Description Change.\n" +
                "  ID: ${event.clusterId.value}\n" +
                "  Prev. Description: ${event.previousDescription}\n" +
                "  New Description: ${event.newDescription}")
    }

    override fun clusterClosed(event: ClusterClosedEvent) {
        logger.info("Cluster Closed.\n" +
                "  ID: ${event.clusterId.value}\n" +
                "  Description: ${event.clusterId.description}")
    }
}

class MongoCommandListener : CommandListener {
    private val logger = LoggerFactory.getLogger(this::class.java)
    override fun commandStarted(event: CommandStartedEvent) {
        logger.debug("Command Started.\n" +
                "  Request ID: ${event.requestId}" +
                "  Database: ${event.databaseName}\n" +
                "  Command: ${event.commandName}\n" +
                "  Command Doc: ${event.command}")
    }

    override fun commandSucceeded(event: CommandSucceededEvent) {
        logger.info("Command Succeeded.\n" +
                "  Request ID: ${event.requestId}\n" +
                "  Took: ${event.getElapsedTime(TimeUnit.MILLISECONDS)}ms\n" +
                "  Command: ${event.commandName}\n" +
                "  Response: ${event.response}")
    }

    override fun commandFailed(event: CommandFailedEvent) {
        logger.info("Command Failed.\n" +
                "  Request ID: ${event.requestId}\n" +
                "  Took: ${event.getElapsedTime(TimeUnit.MILLISECONDS)}ms\n" +
                "  Command: ${event.commandName}\n" +
                "  Response: ${event.throwable}")
    }
}

class MongoConnectionPoolListener : ConnectionPoolListener {
    private val logger = LoggerFactory.getLogger(this::class.java)
    override fun connectionAdded(event: ConnectionAddedEvent) {
        logger.info("Connection Added.\n" +
                "  Local ID: ${event.connectionId.localValue}\n" +
                "  Server ID: ${event.connectionId.serverValue}\n" +
                "  Server: ${event.connectionId.serverId}")
    }

    override fun connectionRemoved(event: ConnectionRemovedEvent) {
        logger.info("Connection Removed.\n" +
                "  Local ID: ${event.connectionId.localValue}\n" +
                "  Server ID: ${event.connectionId.serverValue}\n" +
                "  Server: ${event.connectionId.serverId}")
    }

    override fun connectionCheckedIn(event: ConnectionCheckedInEvent) {
        logger.info("Connection Checked In.\n" +
                "  Local ID: ${event.connectionId.localValue}\n" +
                "  Server ID: ${event.connectionId.serverValue}\n" +
                "  Server: ${event.connectionId.serverId}")
    }

    override fun connectionCheckedOut(event: ConnectionCheckedOutEvent) {
        logger.info("Connection Checked Out.\n" +
                "  Local ID: ${event.connectionId.localValue}\n" +
                "  Server ID: ${event.connectionId.serverValue}\n" +
                "  Server: ${event.connectionId.serverId}")
    }

    override fun connectionPoolOpened(event: ConnectionPoolOpenedEvent) {
        logger.info("Connection Added.\n" +
                "  Server: ${event.serverId}\n" +
                "  Settings: ${event.settings}")
    }

    override fun connectionPoolClosed(event: ConnectionPoolClosedEvent) {
        logger.info("Connection Removed.\n" +
                "  Server: ${event.serverId}")
    }

    override fun waitQueueEntered(event: ConnectionPoolWaitQueueEnteredEvent) {
        logger.info("Wait Queue Entered.\n" +
                "  Server: ${event.serverId}")
    }

    override fun waitQueueExited(event: ConnectionPoolWaitQueueExitedEvent) {
        logger.info("Wait Queue Exited.\n" +
                "  Server: ${event.serverId}")
    }
}

class MongoServerListener : ServerListener {
    private val logger = LoggerFactory.getLogger(this::class.java)
    override fun serverOpening(event: ServerOpeningEvent) {
        logger.info("Server Opening.\n" +
                "  Address: ${event.serverId.address}\n" +
                "  Cluster ID: ${event.serverId.clusterId.value}")
    }

    override fun serverDescriptionChanged(event: ServerDescriptionChangedEvent) {
        logger.debug("Cluster Description Change.\n" +
                "  Address: ${event.serverId.address}\n" +
                "  Cluster ID: ${event.serverId.clusterId.value}" +
                "  Prev. Description: ${event.previousDescription}\n" +
                "  New Description: ${event.newDescription}")
    }

    override fun serverClosed(event: ServerClosedEvent) {
        logger.info("Server Closed.\n" +
                "  Address: ${event.serverId.address}\n" +
                "  Cluster ID: ${event.serverId.clusterId.value}")
    }
}