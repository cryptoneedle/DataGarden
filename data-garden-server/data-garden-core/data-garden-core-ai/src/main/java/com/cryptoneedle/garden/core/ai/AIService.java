package com.cryptoneedle.garden.core.ai;

import com.cryptoneedle.garden.common.key.doris.DorisColumnKey;
import com.cryptoneedle.garden.common.key.doris.OdsColumnTranslateKey;
import com.cryptoneedle.garden.core.crud.SelectService;
import com.cryptoneedle.garden.core.mapping.MappingService;
import com.cryptoneedle.garden.core.ods.OdsService;
import com.cryptoneedle.garden.infrastructure.entity.ods.OdsColumn;
import com.cryptoneedle.garden.infrastructure.entity.ods.OdsColumnTranslate;
import com.cryptoneedle.garden.infrastructure.vo.ods.ColumnTranslateResultVo;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>description:  </p>
 *
 * @author CryptoNeedle
 * @date 2026-04-30
 */
@Service
public class AIService {
    
    @Autowired
    private SelectService selectService;
    @Autowired
    private OdsService odsService;
    @Autowired
    private MappingService mappingService;
    @Autowired
    private ChatClient chatClient;
    
    public void translateOdsColumn(String tableName, String columnName) {
        String ods = selectService.config.dorisSchemaOds();
        OdsColumn odsColumn = selectService.ods.column(new DorisColumnKey(ods, tableName, columnName));
        String comment = odsColumn.getComment();
        var outputConverter = new BeanOutputConverter<>(ColumnTranslateResultVo.class);
        ColumnTranslateResultVo vo = chatClient.prompt()
                                               .system("你是一个数据治理专家。你的任务是提取字段说明中的关键信息。")
                                               .user(u -> u.text("""
                                                                         以下内容是一个表字段的说明，需要提取出字段的值和翻译的值。
                                                                         
                                                                         字段：{columnName}
                                                                         说明：{comment}
                                                                         
                                                                         ----------------
                                                                         {format}
                                                                         """)
                                                           .param("columnName", columnName)
                                                           .param("comment", comment)
                                                           .param("format", outputConverter.getFormat()))
                                               .call()
                                               .entity(outputConverter);
        List<ColumnTranslateResultVo.ColumnTranslateResult> results = null;
        if (vo != null && !vo.getResults().isEmpty()) {
            List<OdsColumnTranslate> odsColumnTranslateList = vo.getResults().stream().map(translate -> {
                OdsColumnTranslateKey key = OdsColumnTranslateKey.builder()
                                                                 .databaseName(ods)
                                                                 .tableName(tableName)
                                                                 .columnName(columnName)
                                                                 .value(translate.getValue())
                                                                 .build();
                return OdsColumnTranslate.builder()
                                         .id(key)
                                         .translate(translate.getTranslateValue())
                                         .build();
            }).toList();
            odsService.saveTranslateColumnList(tableName, columnName, odsColumnTranslateList);
        }
    }
}