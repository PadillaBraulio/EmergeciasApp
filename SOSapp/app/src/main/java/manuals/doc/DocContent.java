package manuals.doc;


public class DocContent {


    public static final DummyItem ITEMS []= {
            new DummyItem("1","Primeros auxilios", "Breve descripción acerca de primeros auxilios."),
            new DummyItem("2","Quebraduras", "Qué hacer si ocurre una quebradura."),
    };

    private static final int COUNT = ITEMS.length;



    public static class DummyItem {
        public final String id;
        public final String content;
        public final String summary;

        public DummyItem(String id, String content, String summary) {
            this.id = id;
            this.content = content;
            this.summary = summary;
        }

        @Override
        public String toString() {
            return content;
        }
    }
}
