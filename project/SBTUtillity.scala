import java.io.File
import java.nio.file.{Files, StandardCopyOption}

import sbt.internal.util.ManagedLogger

object SBTUtillity {

  /**
    * Copies the resources that should be packed with the jar to the resources directory
    */
  def includeResources(logger: ManagedLogger,  resourceDir: File, additionalResources: Seq[File]): Unit = {
    logger info "Copying additional deployment files to resources dir"
    additionalResources.foreach(file => {
      if (file.exists()) {
        Files.copy(file.toPath, new File(resourceDir, file.getName).toPath, StandardCopyOption.REPLACE_EXISTING)
        logger info s"Copied ${file.getName} to ${resourceDir.getPath}/${file.getName}"
      } else {
        logger warn s"Unable to find deployment file ${file.getPath}"
      }
    })
  }
}
