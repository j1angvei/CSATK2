package cn.j1angvei.castk2.stat;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Wayne on 4/21 0021.
 */
public class PeakAnnoColumn implements Column {
    private String expCode;
    private Map<String, Long> map;

    public PeakAnnoColumn(String expCode) {
        this.expCode = expCode;
        map = new HashMap<>();
        for (Type type : Type.values()) {
            map.put(type.getTypeName(), 0L);
        }
    }

    public String getExpCode() {
        return expCode;
    }

    public Map<String, Long> getMap() {
        return map;
    }

    public void setMap(Map<String, Long> map) {
        this.map = map;
    }

    @Override
    public String getHeader() {
        return String.format("Sample\t%s\t%s\t%s\t%s\t%s",
                Type.INTERGENIC.getTypeName(),
                Type.EXON.getTypeName(),
                Type.PROMOTER_TSS.getTypeName(),
                Type.TTS.getTypeName(),
                Type.INTRON.getTypeName()
        );
    }

    @Override
    public String toString() {
        return String.format("%s\t%,d\t%,d\t%,d\t%,d\t%,d\n",
                expCode,
                map.get(Type.INTERGENIC.getTypeName()),
                map.get(Type.EXON.getTypeName()),
                map.get(Type.PROMOTER_TSS.getTypeName()),
                map.get(Type.TTS.getTypeName()),
                map.get(Type.INTRON.getTypeName())
        );
    }

    public enum Type {
        INTERGENIC("Intergenic"),
        EXON("exon"),
        PROMOTER_TSS("promoter-TSS"),
        TTS("TTS"),
        INTRON("intron");

        private String typeName;

        Type(String typeName) {
            this.typeName = typeName;
        }

        public static String asHeader() {
            return String.format("Sample\t%s\t%s\t%s\t%s\t%s",
                    INTERGENIC.getTypeName(),
                    EXON.getTypeName(),
                    PROMOTER_TSS.getTypeName(),
                    TTS.getTypeName(),
                    INTRON.getTypeName());
        }

        public String getTypeName() {
            return typeName;
        }
    }

}
