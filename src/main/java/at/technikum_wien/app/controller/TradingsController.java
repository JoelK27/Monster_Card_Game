package at.technikum_wien.app.controller;

import at.technikum_wien.httpserver.http.ContentType;
import at.technikum_wien.httpserver.http.HttpStatus;
import at.technikum_wien.httpserver.server.Request;
import at.technikum_wien.httpserver.server.Response;
import at.technikum_wien.app.models.TradingDeal;
import at.technikum_wien.app.dal.UnitOfWork;
import at.technikum_wien.app.dal.repository.UserRepository;
import at.technikum_wien.app.models.User;

import java.util.ArrayList;
import java.util.List;

public class TradingsController extends Controller {

    public TradingsController() {

    }

    public Response getTradingDeals(Request request) {
        try (UnitOfWork unitOfWork = new UnitOfWork()) {
            List<User> users = (List<User>) new UserRepository(unitOfWork).findAllUsers();
            List<TradingDeal> tradingDeals = new ArrayList<>();

            for (User user : users) {
                tradingDeals.addAll(user.getTradingDeals());
            }

            String tradingDealsJSON = this.getObjectMapper().writeValueAsString(tradingDeals);
            unitOfWork.commitTransaction();
            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    tradingDealsJSON
            );
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.JSON,
                    "{ \"message\" : \"Internal Server Error\" }"
            );
        }
    }

    public Response createTradingDeal(Request request) {
        try (UnitOfWork unitOfWork = new UnitOfWork()) {
            String token = request.getHeaders().getHeader("Authorization").replace("Bearer ", "");
            User user = new UserRepository(unitOfWork).findUserByToken(token);
            if (user == null) {
                return new Response(
                        HttpStatus.UNAUTHORIZED,
                        ContentType.JSON,
                        "{ \"message\": \"Unauthorized\" }"
                );
            }

            TradingDeal tradingDeal = this.getObjectMapper().readValue(request.getBody(), TradingDeal.class);
            user.addTradingDeal(tradingDeal);
            new UserRepository(unitOfWork).update(user);
            unitOfWork.commitTransaction();
            return new Response(
                    HttpStatus.CREATED,
                    ContentType.JSON,
                    "{ \"message\": \"Trading deal created successfully\" }"
            );
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.JSON,
                    "{ \"message\" : \"Internal Server Error\" }"
            );
        }
    }

    public Response trade(Request request) {
        try (UnitOfWork unitOfWork = new UnitOfWork()) {
            String token = request.getHeaders().getHeader("Authorization").replace("Bearer ", "");
            User user = new UserRepository(unitOfWork).findUserByToken(token);
            if (user == null) {
                return new Response(
                        HttpStatus.UNAUTHORIZED,
                        ContentType.JSON,
                        "{ \"message\": \"Unauthorized\" }"
                );
            }

            String tradingDealId = request.getPathParts().get(1);
            TradingDeal tradingDeal = findTradingDealById(tradingDealId, unitOfWork);
            if (tradingDeal == null) {
                return new Response(
                        HttpStatus.NOT_FOUND,
                        ContentType.JSON,
                        "{ \"message\": \"Trading deal not found\" }"
                );
            }

            User tradePartner = new UserRepository(unitOfWork).findUserByToken(tradingDeal.getPartnerToken());
            if (tradePartner == null) {
                return new Response(
                        HttpStatus.NOT_FOUND,
                        ContentType.JSON,
                        "{ \"message\": \"Trade partner not found\" }"
                );
            }

            // Use tradeCard method to perform the trade
            user.tradeCard(tradingDeal.getOfferedCard(), tradePartner);

            new UserRepository(unitOfWork).update(user);
            new UserRepository(unitOfWork).update(tradePartner);

            unitOfWork.commitTransaction();
            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    "{ \"message\": \"Trade successful\" }"
            );
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.JSON,
                    "{ \"message\" : \"Internal Server Error\" }"
            );
        }
    }

    public Response deleteTradingDeal(Request request) {
        try (UnitOfWork unitOfWork = new UnitOfWork()) {
            String token = request.getHeaders().getHeader("Authorization").replace("Bearer ", "");
            User user = new UserRepository(unitOfWork).findUserByToken(token);
            if (user == null) {
                return new Response(
                        HttpStatus.UNAUTHORIZED,
                        ContentType.JSON,
                        "{ \"message\": \"Unauthorized\" }"
                );
            }

            String tradingDealId = request.getPathParts().get(1);
            TradingDeal tradingDeal = findTradingDealById(tradingDealId, unitOfWork);
            if (tradingDeal == null) {
                return new Response(
                        HttpStatus.NOT_FOUND,
                        ContentType.JSON,
                        "{ \"message\": \"Trading deal not found\" }"
                );
            }

            user.removeTradingDeal(tradingDeal);
            new UserRepository(unitOfWork).update(user);
            unitOfWork.commitTransaction();
            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    "{ \"message\": \"Trading deal deleted successfully\" }"
            );
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.JSON,
                    "{ \"message\" : \"Internal Server Error\" }"
            );
        }
    }

    private TradingDeal findTradingDealById(String tradingDealId, UnitOfWork unitOfWork) {
        List<User> users = (List<User>) new UserRepository(unitOfWork).findAllUsers();
        for (User user : users) {
            for (TradingDeal deal : user.getTradingDeals()) {
                if (deal.getId().equals(tradingDealId)) {
                    return deal;
                }
            }
        }
        return null;
    }
}