package at.technikum_wien.app.controller;

import at.technikum_wien.app.dal.UnitOfWork;
import at.technikum_wien.app.dal.repository.CardRepository;
import at.technikum_wien.app.dal.repository.TradingDealRepository;
import at.technikum_wien.app.dal.repository.UserRepository;
import at.technikum_wien.app.models.Card;
import at.technikum_wien.app.models.TradingDeal;
import at.technikum_wien.app.models.User;
import at.technikum_wien.httpserver.http.ContentType;
import at.technikum_wien.httpserver.http.HttpStatus;
import at.technikum_wien.httpserver.server.Request;
import at.technikum_wien.httpserver.server.Response;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.UUID;

public class TradingsController extends Controller {
    private final ObjectMapper objectMapper = new ObjectMapper();

    public TradingsController() {
    }

    public Response getTradingDeals(Request request) {
        try (UnitOfWork unitOfWork = new UnitOfWork()) {
            TradingDealRepository tradingDealRepository = new TradingDealRepository(unitOfWork);
            List<TradingDeal> tradingDeals = tradingDealRepository.findAll();

            String tradingDealsJSON = this.objectMapper.writeValueAsString(tradingDeals);
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

            TradingDeal tradingDeal = this.objectMapper.readValue(request.getBody(), TradingDeal.class);
            tradingDeal.setOwnerId(user.getID());
            new TradingDealRepository(unitOfWork).save(tradingDeal);
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
            TradingDeal tradingDeal = new TradingDealRepository(unitOfWork).findById(UUID.fromString(tradingDealId));
            if (tradingDeal == null) {
                return new Response(
                        HttpStatus.NOT_FOUND,
                        ContentType.JSON,
                        "{ \"message\": \"Trading deal not found\" }"
                );
            }

            if (tradingDeal.getOwnerId() != user.getID()) {
                return new Response(
                        HttpStatus.FORBIDDEN,
                        ContentType.JSON,
                        "{ \"message\": \"You are not the owner of this trading deal\" }"
                );
            }

            new TradingDealRepository(unitOfWork).delete(UUID.fromString(tradingDealId));
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

    public Response executeTradingDeal(Request request) {
        try (UnitOfWork unitOfWork = new UnitOfWork()) {
            String token = request.getHeaders().getHeader("Authorization").replace("Bearer ", "");
            User buyer = new UserRepository(unitOfWork).findUserByToken(token);
            if (buyer == null) {
                return new Response(
                        HttpStatus.UNAUTHORIZED,
                        ContentType.JSON,
                        "{ \"message\": \"Unauthorized\" }"
                );
            }

            String tradingDealId = request.getPathParts().get(1);
            TradingDeal tradingDeal = new TradingDealRepository(unitOfWork).findById(UUID.fromString(tradingDealId));
            if (tradingDeal == null) {
                return new Response(
                        HttpStatus.NOT_FOUND,
                        ContentType.JSON,
                        "{ \"message\": \"Trading deal not found\" }"
                );
            }

            if (tradingDeal.getOwnerId() == buyer.getID()) {
                return new Response(
                        HttpStatus.BAD_REQUEST,
                        ContentType.JSON,
                        "{ \"message\": \"You cannot trade with yourself\" }"
                );
            }

            UUID offeredCardId = this.objectMapper.readValue(request.getBody(), UUID.class);
            CardRepository cardRepository = new CardRepository(unitOfWork);
            Card offeredCard = cardRepository.findById(offeredCardId);
            if (offeredCard == null) {
                return new Response(
                        HttpStatus.BAD_REQUEST,
                        ContentType.JSON,
                        "{ \"message\": \"Offered card not found in database\" }"
                );
            }

            if (!offeredCard.getType().equalsIgnoreCase(tradingDeal.getType()) || offeredCard.getDamage() < tradingDeal.getMinimumDamage()) {
                return new Response(
                        HttpStatus.BAD_REQUEST,
                        ContentType.JSON,
                        "{ \"message\": \"Offered card does not meet the requirements\" }"
                );
            }

            UserRepository userRepository = new UserRepository(unitOfWork);
            User owner = userRepository.findUserById(tradingDeal.getOwnerId());
            if (owner == null) {
                return new Response(
                        HttpStatus.NOT_FOUND,
                        ContentType.JSON,
                        "{ \"message\": \"Owner not found\" }"
                );
            }

            Card cardToTrade = cardRepository.findById(tradingDeal.getCardToTrade());
            if (cardToTrade == null) {
                return new Response(
                        HttpStatus.NOT_FOUND,
                        ContentType.JSON,
                        "{ \"message\": \"Card to trade not found in database\" }"
                );
            }

            buyer.tradeCard(offeredCard, owner);
            owner.tradeCard(cardToTrade, buyer);

            userRepository.update(buyer);
            userRepository.update(owner);
            new TradingDealRepository(unitOfWork).delete(tradingDeal.getId());
            unitOfWork.commitTransaction();

            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    "{ \"message\": \"Trade executed successfully\" }"
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
}