package com.obsidiandynamics.yconf;

/**
 *  Represents the default {@link TypeMapper} class used in the {@link YInject} annotation.
 *  This class will be swapped with an actual {@link TypeMapper} present in the 
 *  {@link MappingContext} registry.<p>
 *  
 *  This class is not meant to be instantiated or extended.
 */
abstract class NullMapper implements TypeMapper {
  private NullMapper() {}
}
