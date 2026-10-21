package TaskTree;

public class enums{
	public enum Category {
        USER("Usuário"), ABSTRACTION("Abstrata"), APPLICATION("Aplicação"), INTERACTION("Interação");
        private final String label;
        Category(String label) { this.label = label; }
        public String toString() { return label; }
    }
	public enum Links{CHOICE, ORDERINDEPENDANCE}
	public enum ButtonType{ SHOWOPTIONS, SHOWOPTIONSTASKS, SHOWOPTIONSLINKS, EDITTASK, REMOVETASK,
		ADDABSTRACTIONTASK, ADDAPPLICATIONTASK, ADDCHOICELINK, ADDINTERACTIONTASK, ADDORDERLINK,
		ADDUSERTASK, REMOVELINK}
}
