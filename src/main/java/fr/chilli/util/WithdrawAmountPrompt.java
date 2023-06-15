package fr.chilli.util;

import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

public class WithdrawAmountPrompt extends StringPrompt {

    @Override
    public String getPromptText(ConversationContext context) {
        return "Montant : ";
    }

    @Override
    public Prompt acceptInput(ConversationContext context, String input) {
        Player player = (Player) context.getForWhom();

        // Vérifier si l'entrée est un nombre valide
        double amount;
        try {
            amount = Double.parseDouble(input);
        } catch (NumberFormatException e) {
            player.sendMessage("Montant invalide. Veuillez saisir un nombre valide.");
            return this;
        }

        // Vérifier si le montant est positif
        if (amount <= 0) {
            player.sendMessage("Montant invalide. Veuillez saisir un montant supérieur à zéro.");
            return this;
        }

        // Afficher une fenêtre de confirmation pour le retrait
        ConfirmationGUI.openConfirmationMenu(player, amount, ConfirmationGUI.ConfirmationType.WITHDRAW);

        return Prompt.END_OF_CONVERSATION;
    }
}

