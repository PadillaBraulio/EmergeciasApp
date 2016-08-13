package com.emergenciasapp.manuals.doc;


public class DocContent {


    public static final Document DOCUMENTS[]= {
            new Document("1","Primeros auxilios", "Breve descripción acerca de primeros auxilios.","manual.pdf"),
            new Document("2","Fracturas", "Qué hacer si ocurre una Fractura.",null),
    };
    private static final int COUNT = DOCUMENTS.length;



    public static class Document {
        public final String id;
        public final String content;
        public final String summary;
        public final String filename;

        public Document(String id, String content, String summary, String filename) {
            this.id = id;
            this.content = content;
            this.summary = summary;
            this.filename = filename;
        }

        @Override
        public String toString() {
            return  id + " : Titulo : " + content + " , summary : " + summary + " filename " + filename;
        }
    }
}
