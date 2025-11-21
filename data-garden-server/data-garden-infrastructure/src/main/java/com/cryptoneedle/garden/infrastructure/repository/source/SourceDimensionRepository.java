package com.cryptoneedle.garden.infrastructure.repository.source;

import com.bubbles.engine.data.core.repository.BaseRepository;
import com.cryptoneedle.garden.common.enums.SourceDimensionType;
import com.cryptoneedle.garden.common.key.source.SourceDimensionKey;
import com.cryptoneedle.garden.infrastructure.entity.source.SourceDimension;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>description: 数据源-维度-存储 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-20
 */
@Repository
public interface SourceDimensionRepository extends BaseRepository<SourceDimension, SourceDimensionKey> {

    @Query("""
              FROM SourceDimension
             WHERE id.catalogName = :catalogName
               AND id.databaseName = :databaseName
               AND id.tableName = :tableName
               AND id.dimensionType = :dimensionType
               AND id.dimensionName = :dimensionName
               AND id.columnName = :columnName
            """)
    SourceDimension dimension(String catalogName, String databaseName, String tableName, SourceDimensionType dimensionType, String dimensionName, String columnName);

    @Query("""
             FROM SourceDimension
            ORDER BY id.catalogName, id.databaseName, id.tableName, enabled DESC, id.dimensionType, id.dimensionName, sort
            """)
    List<SourceDimension> dimensions();

    @Query("""
             FROM SourceDimension
            WHERE id.catalogName = :catalogName
            ORDER BY id.catalogName, id.databaseName, id.tableName, enabled DESC, id.dimensionType, id.dimensionName, sort
            """)
    List<SourceDimension> dimensions(String catalogName);

    @Query("""
             FROM SourceDimension
            WHERE id.catalogName = :catalogName
              AND id.databaseName = :databaseName
            ORDER BY id.catalogName, id.databaseName, id.tableName, enabled DESC, id.dimensionType, id.dimensionName, sort
            """)
    List<SourceDimension> dimensions(String catalogName, String databaseName);

    @Query("""
             FROM SourceDimension
            WHERE id.catalogName = :catalogName
              AND id.databaseName = :databaseName
              AND id.tableName = :tableName
            ORDER BY id.catalogName, id.databaseName, id.tableName, enabled DESC, id.dimensionType, id.dimensionName, sort
            """)
    List<SourceDimension> dimensions(String catalogName, String databaseName, String tableName);

    @Query("""
             FROM SourceDimension
            WHERE id.catalogName = :catalogName
              AND id.databaseName = :databaseName
              AND id.tableName = :tableName
              AND id.dimensionType = :dimensionType
            ORDER BY id.catalogName, id.databaseName, id.tableName, enabled DESC, id.dimensionType, id.dimensionName, sort
            """)
    List<SourceDimension> dimensions(String catalogName, String databaseName, String tableName, SourceDimensionType dimensionType);

    @Query("""
             FROM SourceDimension
            WHERE id.catalogName = :catalogName
              AND id.databaseName = :databaseName
              AND id.tableName = :tableName
              AND id.dimensionType = :dimensionType
              AND id.dimensionName = :dimensionName
            ORDER BY id.catalogName, id.databaseName, id.tableName, enabled DESC, id.dimensionType, id.dimensionName, sort
            """)
    List<SourceDimension> dimensions(String catalogName, String databaseName, String tableName, SourceDimensionType dimensionType, String dimensionName);

    @Query("""
             FROM SourceDimension
            WHERE id.catalogName = :catalogName
              AND id.databaseName = :databaseName
              AND id.tableName = :tableName
              AND id.columnName = :columnName
            ORDER BY id.catalogName, id.databaseName, id.tableName, enabled DESC, id.dimensionType, id.dimensionName, sort
            """)
    List<SourceDimension> dimensions(String catalogName, String databaseName, String tableName, String columnName);

    @Query("""
             FROM SourceDimension
            WHERE id.catalogName = :catalogName
              AND id.databaseName = :databaseName
              AND id.tableName = :tableName
              AND enabled = TRUE
            ORDER BY id.catalogName, id.databaseName, id.tableName, enabled DESC, id.dimensionType, id.dimensionName, sort
            """)
    List<SourceDimension> dimensionsEnabled(String catalogName, String databaseName, String tableName);
}