package com.jeffrick.grails.plugins.services

import com.jeffrick.grails.plugin.sharding.CurrentShard
import com.jeffrick.grails.plugin.sharding.ShardConfig
import com.jeffrick.grails.plugin.sharding.Shards
import com.jeffrick.grails.plugin.sharding.Index
import com.jeffrick.grails.plugins.sharding.Shard

// import com.jeffrick.grails.plugin.sharding.Index

/**
 * @author <a href='mailto:jeffrick@gmail.com'>Jeff Rick</a>
 */
class ShardService {
  def sessionFactory

  // This needs to be false otherwise all connections create will always be transactional
  boolean transactional = false

  def getShard(object) {
    Index currentIndex = Index.get()
    Shard currentShard

    // If we don't have a shard assigned go assign one
    if(object."$currentIndex.shardNameFieldName" == null) {
      currentShard = getNextShard()
      object."$currentIndex.shardNameFieldName" = currentShard.shardName
    } else {
      currentShard = Shard.findByShardName(object."$currentIndex.shardNameFieldName")
    }

    return(currentShard)
  }

  def changeByObject(object) {
    def currentShard = getShard(object)
    change(currentShard.shardName)
  }

  def change(String name) {
    Shards.shards.each {
      if (it.name == name) {
        change it
      }
    }
  }

  def change(ShardConfig shard) {
    if (shard) {
      if (shard.name != CurrentShard.getName()) {
        //test connection
        def oldEnv = CurrentShard.getName()

        CurrentShard.setShard shard

        try {
          CurrentShard.setAutoCommit sessionFactory.getCurrentSession().jdbcContext.hibernateTransaction == null
        } catch (org.hibernate.HibernateException he) {
          CurrentShard.setAutoCommit true
        } catch (Exception e) {
          log.error "Error: ${e}"
        }

        try {
          try {
            sessionFactory.currentSession.clear()
            sessionFactory.currentSession.disconnect()
          } catch (Exception e) {
            //  e.printStackTrace()
          }
          return true
        } catch (e) {
          CurrentShard.setShard(Shards.getShards().find {oldEnv == it.name})
          log.error "Unable to connect to database" + e.message
          throw new Exception("Unable to connect to database + " + e.message)
        }
      }
      return true
    } else {
      throw new Exception("No shardName provided")
    }
  }

  def Shard getNextShard() {
    def c = Shard.createCriteria()
    def shards = c {
      order("ratio", "asc")
      maxResults(1)
    }
    return (shards[0])
  }
}
