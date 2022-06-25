package models;

public class BurgerComposition {

    public String getSuperBurger(){
        return "{\"ingredients\":[" +
                //bun: Флюоресцентная булка R2-D3
                "\"61c0c5a71d1f82001bdaaa6d\"," +
                //main: Сыр с астероидной плесенью, Мини-салат Экзо-Плантаго, Плоды Фалленианского дерева,
                "\"61c0c5a71d1f82001bdaaa7a\",\"61c0c5a71d1f82001bdaaa79\",\"61c0c5a71d1f82001bdaaa77\"," +
                //sauce: Соус с шипами Антарианского плоскоходца, Соус Spicy-X
                "\"61c0c5a71d1f82001bdaaa75\",\"61c0c5a71d1f82001bdaaa72\"," +
                //main: Филе Люминесцентного тетраодонтимформа, Биокотлета из марсианской Магнолии
                "\"61c0c5a71d1f82001bdaaa6e\",\"61c0c5a71d1f82001bdaaa71\"]}";
    }

    public String getBigBurger(){
        return "{\"ingredients\":[" +
                //bun: Флюоресцентная булка R2-D3
                "\"61c0c5a71d1f82001bdaaa6d\"," +
                //sauce: Соус с шипами Антарианского плоскоходца, Соус Spicy-X
                "\"61c0c5a71d1f82001bdaaa75\",\"61c0c5a71d1f82001bdaaa72\"," +
                //main: Филе Люминесцентного тетраодонтимформа, Мясо бессмертных моллюсков Protostomia
                "\"61c0c5a71d1f82001bdaaa6e\",\"61c0c5a71d1f82001bdaaa6f\"]}";
    }

    public String getStandardBurger(){
        return "{\"ingredients\":[" +
                //bun: Краторная булка N-200i
                "\"61c0c5a71d1f82001bdaaa6c\"," +
                //sauce: Соус традиционный галактический
                "\"61c0c5a71d1f82001bdaaa74\"," +
                //main: Говяжий метеорит (отбивная)
                "\"61c0c5a71d1f82001bdaaa70\"]}";
    }

    public String getMiniBurger(){
        //bun: Краторная булка N-200i
        //main: Биокотлета из марсианской Магнолии
        return "{\"ingredients\": [\"61c0c5a71d1f82001bdaaa6c\",\"61c0c5a71d1f82001bdaaa71\"]}";
    }

    public String getBun(){
        return "{\"ingredients\": [\"61c0c5a71d1f82001bdaaa6c\"]}"; //bun: Краторная булка N-200i
    }

    public String getMain(){
        return "{\"ingredients\": [\"61c0c5a71d1f82001bdaaa6f\"]}"; //main: Мясо бессмертных моллюсков Protostomia
    }

    public String getSauce(){
        return "{\"ingredients\": [\"61c0c5a71d1f82001bdaaa74\"]}"; //sauce: Соус традиционный галактический
    }

    public String getDoubleBun(){
        return "{\"ingredients\":[\"61c0c5a71d1f82001bdaaa6c\",\"61c0c5a71d1f82001bdaaa6d\"]}"; //bun: Краторная булка N-200i, Флюоресцентная булка R2-D3
    }

    public String getNoIngredients(){
        return "{\"ingredients\":[]}";
    }

    public String getEmptyJson(){
        return "{}";
    }

    public String getIncorrect(){
        return "{\"ingredients\":[\"61c01bi7y70jnkj001bdaa\"]}";
    }
}
