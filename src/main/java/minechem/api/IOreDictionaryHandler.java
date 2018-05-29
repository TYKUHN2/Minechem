package minechem.api;

public interface IOreDictionaryHandler
{
    boolean canHandle(String oreName);

    void handle(String oreName);
}
