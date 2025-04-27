package com.spq.vinted.controller;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;
import org.mockito.InOrder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.spq.vinted.dto.ChatMessageDTO;
import com.spq.vinted.service.MessageService;

@ExtendWith(MockitoExtension.class)
public class ChatControllerTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private MessageService messageService;

    @InjectMocks
    private ChatController chatController;

    private ChatMessageDTO testMessage;

    @BeforeEach
    void setUp() {
        testMessage = new ChatMessageDTO();
        testMessage.setChatRoomId(123L);
        testMessage.setContent("Hello World");
        testMessage.setSenderId(1L);
    }

    @Test
    void testSendMessage_Success() {
        // Arrange
        String expectedDestination = "/topic/chat/123";

        // Act
        chatController.sendMessage(testMessage);

        // Assert
        verify(messagingTemplate).convertAndSend(eq(expectedDestination), eq(testMessage));
        verify(messageService).saveMessage(testMessage);
    }

    @Test
    void testSendMessage_WithDifferentChatRoomId() {
        // Arrange
        testMessage.setChatRoomId(456L);
        String expectedDestination = "/topic/chat/456";

        // Act
        chatController.sendMessage(testMessage);

        // Assert
        verify(messagingTemplate).convertAndSend(eq(expectedDestination), eq(testMessage));
        verify(messageService).saveMessage(testMessage);
    }

    @Test
    void testSendMessage_VerifyOrder() {
        // Arrange
        String expectedDestination = "/topic/chat/123";

        // Act
        chatController.sendMessage(testMessage);

        // Assert - Verify the order of operations
        InOrder inOrder = inOrder(messagingTemplate, messageService);
        inOrder.verify(messagingTemplate).convertAndSend(eq(expectedDestination), eq(testMessage));
        inOrder.verify(messageService).saveMessage(testMessage);
    }


    @Test
    void testSendMessage_ExceptionInMessageService() {
        // Arrange
        String expectedDestination = "/topic/chat/123";
        doThrow(new RuntimeException("Service error")).when(messageService).saveMessage(any());

        // Act & Assert
        try {
            chatController.sendMessage(testMessage);
        } catch (RuntimeException e) {
            // Expected exception
        }

        // Assert - Message is still sent even if saving fails
        verify(messagingTemplate).convertAndSend(eq(expectedDestination), eq(testMessage));
        verify(messageService).saveMessage(testMessage);
    }

    @Test
    void testSendMessage_ExceptionInMessagingTemplate() {
        // Arrange
        String expectedDestination = "/topic/chat/123";
        doThrow(new RuntimeException("Messaging error")).when(messagingTemplate).convertAndSend(anyString(), any(ChatMessageDTO.class));

        // Act & Assert
        try {
            chatController.sendMessage(testMessage);
        } catch (RuntimeException e) {
            // Expected exception
        }

        // Assert - Exception occurs before saving
        verify(messagingTemplate).convertAndSend(eq(expectedDestination), eq(testMessage));
        verify(messageService, never()).saveMessage(any());
    }

    @Test
    void testSendMessage_WithNullMessage() {
        // This test verifies that the method handles null messages
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            chatController.sendMessage(null);
        });
        
        // Verify that no interactions occurred with the mocks when message is null
        verify(messagingTemplate, never()).convertAndSend(anyString(), any(ChatMessageDTO.class));
        verify(messageService, never()).saveMessage(any());
    }

    @Test
    void testSendMessage_EmptyContent() {
        // Arrange
        testMessage.setContent("");
        String expectedDestination = "/topic/chat/123";

        // Act
        chatController.sendMessage(testMessage);

        // Assert
        verify(messagingTemplate).convertAndSend(eq(expectedDestination), eq(testMessage));
        verify(messageService).saveMessage(testMessage);
    }

    @Test
    void testSendMessage_ZeroChatRoomId() {
        // Arrange
        testMessage.setChatRoomId(0L);
        String expectedDestination = "/topic/chat/0";

        // Act
        chatController.sendMessage(testMessage);

        // Assert
        verify(messagingTemplate).convertAndSend(eq(expectedDestination), eq(testMessage));
        verify(messageService).saveMessage(testMessage);
    }
}