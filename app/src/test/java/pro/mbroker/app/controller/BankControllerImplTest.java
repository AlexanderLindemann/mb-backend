//package pro.mbroker.app.controller;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.transaction.annotation.Transactional;
//import pro.mbroker.app.model.bank.Bank;
//import pro.mbroker.app.model.bank.BankRepository;
//
//import java.util.Arrays;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@AutoConfigureMockMvc
//@Transactional
//@SpringBootTest
//public class BankControllerImplTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private BankRepository bankRepository;
//
//    @Test
//    public void testCreateBank() throws Exception {
//        // Создаем несколько банков с разными именами
//        Bank bank1 = new Bank().setName("Bank1");
//        Bank bank2 = new Bank().setName("Bank2");
//        Bank bank3 = new Bank().setName("Bank3");
//
//        // Добавляем банки в базу данных
//        bankRepository.saveAll(Arrays.asList(bank1, bank2, bank3));
//
//        // Проверяем, что банки сортируются по алфавиту
//        mockMvc.perform(post("/banks")
//                        .param("name", "Bank0")
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.name").value("Bank0"));
//
//        mockMvc.perform(get("/all")
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].name").value("Bank0"))
//                .andExpect(jsonPath("$[1].name").value("Bank1"))
//                .andExpect(jsonPath("$[2].name").value("Bank2"))
//                .andExpect(jsonPath("$[3].name").value("Bank3"));
//    }
//}
