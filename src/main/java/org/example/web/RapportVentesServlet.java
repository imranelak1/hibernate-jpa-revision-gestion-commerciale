package org.example.web;
import org.example.service.RapportVentesService;
import org.example.util.HibernateUtil;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.persistence.EntityManagerFactory;
import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeParseException;
import java.util.stream.Collectors;

@WebServlet("/rapport-ventes")
public class RapportVentesServlet extends HttpServlet {
    private transient RapportVentesService service;
    public RapportVentesServlet() {}
    public RapportVentesServlet(EntityManagerFactory emf) { service=new RapportVentesService(emf); }
    @Override public void init() { if(service==null)service=new RapportVentesService(HibernateUtil.getSessionFactory()); }
    @Override protected void doGet(HttpServletRequest request,HttpServletResponse response) throws IOException {
        response.setContentType("application/json");response.setCharacterEncoding("UTF-8");
        LocalDate date;
        try { date=request.getParameter("date")==null?LocalDate.now(ZoneId.of("Africa/Casablanca")):LocalDate.parse(request.getParameter("date")); }
        catch(DateTimeParseException e) { response.setStatus(400);response.getWriter().write("{\"erreur\":\"Date attendue : AAAA-MM-JJ\"}");return; }
        String ventes=service.moisPrecedent(date).stream().map(v->"{\"categorie\":\""+escape(v.categorie())+"\",\"chiffreAffaires\":"+v.chiffreAffaires().toPlainString()+"}").collect(Collectors.joining(","));
        response.getWriter().write("{\"mois\":\""+YearMonth.from(date.minusMonths(1))+"\",\"ventes\":["+ventes+"]}");
    }
    private static String escape(String text) {
        StringBuilder result=new StringBuilder();
        for(char ch:text.toCharArray()) {
            if(ch=='"'||ch=='\\')result.append('\\').append(ch);
            else if(ch<32)result.append(String.format("\\u%04x",(int)ch));
            else result.append(ch);
        }
        return result.toString();
    }
}
