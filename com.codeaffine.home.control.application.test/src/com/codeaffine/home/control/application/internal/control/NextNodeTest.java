package com.codeaffine.home.control.application.internal.control;

import static com.codeaffine.home.control.application.internal.control.NodeType.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import org.junit.Test;

import com.codeaffine.home.control.application.control.Scene;

public class NextNodeTest {

  @Test
  public void is() {
    NextNode nextNode = new NextNode( mock( Node.class ), AND );

    assertThat( nextNode.is( AND ) ).isTrue();
    assertThat( nextNode.is( CHILD ) ).isFalse();
  }
  @Test
  public void evaluate() {
    Scene expected = new Scene1();
    Node<?> node = stubNode( expected );
    NextNode nextNode = new NextNode( node, SUCCESSOR );

    Scene actual = nextNode.evaluate();

    assertThat( actual ).isSameAs( expected );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsNodeArgument() {
    new NextNode( null, CHILD );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsNodeTypeArgument() {
    new NextNode( mock( Node.class ), null );
  }

  private static Node<?> stubNode( Scene scene ) {
    Node<?> result = mock( Node.class );
    when( result.evaluate() ).thenReturn( scene );
    return result;
  }
}
