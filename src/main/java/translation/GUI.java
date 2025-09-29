package translation;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.awt.event.*;


public class GUI {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LanguageCodeConverter languageConv = new LanguageCodeConverter();
            CountryCodeConverter  countryConv  = new CountryCodeConverter();


            JPanel languagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            languagePanel.add(new JLabel("Language:"));

            List<String> languageNames = new ArrayList<>(languageConv.getAllLanguageNames());
            languageNames.sort(String.CASE_INSENSITIVE_ORDER);
            JComboBox<String> languageCombo = new JComboBox<>(languageNames.toArray(new String[0]));

            // try to default to English if available
            for (int i = 0; i < languageCombo.getItemCount(); i++) {
                if ("English".equalsIgnoreCase(languageCombo.getItemAt(i))) {
                    languageCombo.setSelectedIndex(i);
                    break;
                }
            }
            languagePanel.add(languageCombo);

            JPanel countryPanel = new JPanel(new BorderLayout());
            countryPanel.add(new JLabel("Country:"), BorderLayout.NORTH);

            List<String> countryNames = new ArrayList<>(countryConv.getAllCountries());
            countryNames.sort(String.CASE_INSENSITIVE_ORDER);

            JList<String> countryList = new JList<>(countryNames.toArray(new String[0]));
            countryList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            countryList.setVisibleRowCount(12);
            // default to Canada if present
            int defaultIndex = countryNames.indexOf("Canada");
            countryList.setSelectedIndex(Math.max(defaultIndex, 0));

            JScrollPane countryScroll = new JScrollPane(countryList);
            countryScroll.setPreferredSize(new Dimension(200, 220));
            countryPanel.add(countryScroll, BorderLayout.CENTER);

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JButton submit = new JButton("Submit");
            buttonPanel.add(submit);

            JLabel resultLabelText = new JLabel("Translation:");
            buttonPanel.add(resultLabelText);
            JLabel resultLabel = new JLabel(" ");
            buttonPanel.add(resultLabel);



            // adding listener for when the user clicks the submit button
            Runnable doTranslate = () -> {
                String languageName = (String) languageCombo.getSelectedItem();
                String countryName  = countryList.getSelectedValue();

                if (languageName == null || languageName.isBlank()) {
                    resultLabel.setText("please select a language");
                    return;
                }
                if (countryName == null || countryName.isBlank()) {
                    resultLabel.setText("please select a country");
                    return;
                }

                String languageCode = languageConv.fromLanguage(languageName);
                String countryCode  = countryConv.fromCountry(countryName);

                if (languageCode == null || languageCode.isBlank()) {
                    resultLabel.setText("no language code found for: " + languageName);
                    return;
                }
                if (countryCode == null || countryCode.isBlank()) {
                    resultLabel.setText("no country code found for: " + countryName);
                    return;
                }

                Translator translator = new JSONTranslator();
                String result = translator.translate(countryCode.toLowerCase(), languageCode.toLowerCase());
                resultLabel.setText((result == null || result.isBlank()) ? "no translation found!" : result);
            };

            submit.addActionListener((ActionEvent e) -> doTranslate.run());
            // nice UX: translate when selections change
            languageCombo.addActionListener(e -> doTranslate.run());
            countryList.addListSelectionListener(e -> { if (!e.getValueIsAdjusting()) doTranslate.run();
            });

            JPanel mainPanel = new JPanel();
            mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
            mainPanel.add(languagePanel);
            mainPanel.add(countryPanel);
            mainPanel.add(buttonPanel);

            JFrame frame = new JFrame("Country Name Translator");
            frame.setContentPane(mainPanel);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);


        });
    }
}
