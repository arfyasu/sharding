import org.codehaus.groovy.grails.commons.ApplicationHolder as AH
import org.codehaus.groovy.grails.commons.DefaultGrailsDomainClass

import com.mchange.v2.c3p0.ComboPooledDataSource

import com.jeffrick.grails.plugin.sharding.ShardConfig
import com.jeffrick.grails.plugin.sharding.Index
import com.jeffrick.grails.plugin.sharding.Shards
import com.jeffrick.grails.plugin.sharding.ShardingDS
import com.jeffrick.grails.plugin.sharding.ShardEntityInterceptor
import com.jeffrick.grails.plugins.services.ShardService

import com.burtbeckwith.grails.plugin.datasources.DatasourcesBuilder
import com.burtbeckwith.grails.plugin.datasources.DatasourcesUtils


class ShardingGrailsPlugin {
  def version = "0.6"
  def grailsVersion = "1.2.1 > *"
  def dependsOn = [hibernate: '1.0 > *', datasources: '0.5 > *']
  def pluginExcludes = ["grails-app/views/error.gsp"]
  def author = "Jeff Rick"
  def authorEmail = "jeffrick@gmail.com"
  def title = "Grails Shards Plugin"
  def description = 'Supports sharding of data'
  def documentation = "http://grails.org/plugin/sharding"


  def doWithSpring = {
    // def shards = loadShards()

    def shardDataSources = [:]

    shardDataSources.put(0, ref("dataSource"))

    def dataSources = loadDatasourcesConfig()
    if (!dataSources) {
      return
    }
    int shardId = 1
    def shards = []
    for (ds in dataSources) {
      String name = ds.name
      if (name == "index") {
        Index.init(ds.domainClasses.first())
      } else {
        shardDataSources.put(shardId, ref("dataSource_$name"))
        ShardConfig shardConfig = new ShardConfig()
        shardConfig.id = shardId++
        shardConfig.name = name
        shards.add(shardConfig)
      }
    }

    Shards.shards = shards
    // Shards.shards = loadShardConfig(AH.application)

    // // For each shard create a bean and add it to the shardDataSourcesMap
    // // currently pulling a small number of configuration items for each shard
    // // will expand in the future
    // shards.each {
    //   shard ->

    //   assert shard.name

    //   "dataSource_$shard.name"(ComboPooledDataSource) {
    //     driverClass = shard.driverClass
    //     user = shard.user
    //     password = shard.password
    //     jdbcUrl = shard.jdbcUrl
        
    //     if (shard.initialPoolSize != null) {
    //     		initialPoolSize = shard.initialPoolSize
    //     	}
	
    //   	if (shard.minPoolSize != null) {
    //   		minPoolSize = shard.minPoolSize
    //   	}
      	
    //   	if (shard.maxPoolSize != null) {
    //   		maxPoolSize = shard.maxPoolSize
    //   	}
      	
    //   	testConnectionOnCheckin = shard.testConnectionOnCheckin
    //   	testConnectionOnCheckout = shard.testConnectionOnCheckout
      	
    //   	if (shard.maxIdleTime != null) {
    //   		maxIdleTime = shard.maxIdleTime
    //   	}
      	
    //   	if (shard.maxConnectionAge != null) {
    //   		maxConnectionAge = shard.maxConnectionAge
    //   	}
      	
    //   	if (shard.automaticTestTable != null) {
    //   		automaticTestTable = shard.automaticTestTable
    //   	}
    //   }

    //   shard.id = currentId
    //   shardDataSources.put(currentId++, ref("dataSource_$shard.name"))
    // }

    // Create the dataSource bean that has the Shard specific SwitchableDataSource implementation
    // we also set the targetDataSoures map to the one we built above
    dataSource(ShardingDS) {
      targetDataSources = shardDataSources
    }

    // Define an entityInterceptor that will be triggered when transactions
    // start and end.  This basically coordinates the starting and stopping of transactions
    // between the active shard and the index database
    entityInterceptor(ShardEntityInterceptor)
  }


  def doWithWebDescriptor = {xml -> }

  def doWithDynamicMethods = {ctx ->

    // // Find the domain class the owning application has defined as
    // // the "Index" domain class.  This domain class is used to store
    // // the list of objects and the shard they live in
    // application.domainClasses.each {
    //   DefaultGrailsDomainClass domainClass ->
    //   if (domainClass.fullName.equals(Index.getIndexDomainClass())) {

    //     // For the index domain class add a beforeInsert event handler
    //     // that will assign the next shard to the object being saved.
    //     // In the future will need to be able to chain this event with existing beforeInsert
    //     // event handlers
    //     domainClass.metaClass.beforeInsert = {->
    //       Index currentIndex = Index.get()
    //       ShardService shardService = ctx.getBean('shardService')

    //       // Before we insert we need to figure out the shard to assign ourselves to
    //       def shardObject = shardService.getNextShard()
    //       shardObject.refresh()

    //       // Set the shard on the object
    //       delegate."$currentIndex.shardNameFieldName" = shardObject.shardName

    //       // Increment the usage of the shard assigned
    //       shardObject.incrementUsage()
    //     }

    //   }
    // }
  }

  def doWithApplicationContext = {applicationContext -> }
  def onChange = {event -> }
  def onConfigChange = {event -> }

  // private loadShards() {
  //   try {
  //     def script = AH.application.classLoader.loadClass('Shards').newInstance()
  //     script.run()

  //     def builder = new ShardBuilder()
  //     def shards = script.shards
  //     shards.delegate = builder
  //     shards()

  //     return builder.shards
  //   }
  //   catch (e) {
  //     println e.message
  //     return []
  //   }
  // }

  // private loadShardConfig(def app) {
  //   try {
  //     def shards = []
  //     def dataSourceLookup = [:]
  //     int shardId = 1

  //     def dataSources = loadDatasourcesConfig()
  //     if (!dataSources) {
  //       return
  //     }
  //     for (ds in dataSources) {
  //       println "--> " + ds.name
  //       def value = ref("dataSource_$ds.name")
  //       // // DatasourcesUtils.applicationContext
  //       // def value = DatasourcesUtils.getDataSource(ds.name)
  //       if (value)
  //         println value.url

  //     }

  //     // app.config.each { key, value ->
  //     //   println key
  //       // if (key.startsWith('dataSource_')) {
  //       //   if (value.getProperty("shard")) {
  //       //     ShardConfig shardConfig = new ShardConfig()
  //       //     shardConfig.id = shardId++
  //       //     shardConfig.name = key.replace("dataSource_", "")
  //       //     shards.add(shardConfig)
  //       //   }
  //       //   dataSourceLookup.put(key, value)
  //       // }
  //     // }
  //     // CurrentShard.setDataSourceLookup(dataSourceLookup)
  //     return shards
  //   }
  //   catch (e) {
  //     println e.message
  //     e.printStackTrace()
  //     return []
  //   }
  // }

  private loadDatasourcesConfig() {
    try {
      def script = AH.application.classLoader.loadClass('Datasources').newInstance()
      script.run()
  
      def builder = new DatasourcesBuilder()
      def datasources = script.datasources
      datasources.delegate = builder
      datasources()
  
      return builder.dataSources
    }
    catch (e) {
      println e.message
      return []
    }
  }

}
