package com.paymybuddy.finance;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class SpringWebAppTest {

    @Autowired
    MockMvc mvc;

    /*
     * @Test public void shouldReturnDefaultMessage() throws Exception {
     * mvc.perform(get("/login")).andDo(print()).andExpect(status().isOk()); }
     * 
     * @Test public void userLoginTest() throws Exception {
     * mvc.perform(formLogin("/login").user("person@person.mail").password(
     * "person123")).andExpect(authenticated()); }
     */
}
