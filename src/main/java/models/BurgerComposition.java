package models;

//захардкоженные данные для получения ингрилиентов, цены и названия,
//на случай, если get-запрос на получение ингридиентов бдует не работать
public class BurgerComposition {

    public String getSuperBurger(){
        return "{\"ingredients\":[" +
                //bun: Флюоресцентная булка R2-D3б, Краторная булка N-200i
                "\"61c0c5a71d1f82001bdaaa6d\",\"61c0c5a71d1f82001bdaaa6c\"," +
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
        return "{\"ingredients\":[\"61c0c5a71d1f82001bdaaa6c\",\"61c0c5a71d1f82001bdaaa6c\"]}"; //bun: Краторная булка N-200i 2 шт
    }

    public String getIncorrect(){
        return "{\"ingredients\":[\"61c01bi7y70jnkj001bdaa\"]}";
    }

    public Integer getPrice(String burger){
        Integer price;

        switch (burger){
            case "super":
                price = 13249;
                break;
            case "big":
                price = 3491;
                break;
            case "standard":
                price = 4270;
                break;
            case "mini":
                price = 1679;
                break;
            case "bun":
                price = 1255;
                break;
            case "main":
                price = 1337;
                break;
            case "sauce":
                price = 15;
                break;
            case "double bun":
                price = 2510;
                break;
            default: price = null;
        }

        return price;
    }

    public String getName(String burger){
        String name;

        switch (burger){
            case "super":
                name = "Spicy экзо-плантаго краторный фалленианский люминесцентный антарианский флюоресцентный астероидный био-марсианский бургер";
                break;
            case "big":
                name = "Spicy люминесцентный антарианский флюоресцентный бессмертный бургер";
                break;
            case "standard":
                name = "Метеоритный краторный традиционный-галактический бургер";
                break;
            case "mini":
                name = "Краторный био-марсианский бургер";
                break;
            case "bun":
                name = "Краторный бургер";
                break;
            case "main":
                name = "Бессмертный бургер";
                break;
            case "sauce":
                name = "Традиционный-галактический бургер";
                break;
            case "double bun":
                name = "Краторный бургер";
                break;
            default: name = null;
        }

        return name;
    }
}
